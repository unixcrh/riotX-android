/*
 * Copyright 2019 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.vector.matrix.android.internal.session.room.read

import com.zhuinden.monarchy.Monarchy
import im.vector.matrix.android.internal.database.model.ReadMarkerEntity
import im.vector.matrix.android.internal.database.model.RoomSummaryEntity
import im.vector.matrix.android.internal.database.model.TimelineEventEntity
import im.vector.matrix.android.internal.database.query.*
import im.vector.matrix.android.internal.database.query.latestEvent
import im.vector.matrix.android.internal.database.query.where
import im.vector.matrix.android.internal.di.UserId
import im.vector.matrix.android.internal.network.executeRequest
import im.vector.matrix.android.internal.session.room.RoomAPI
import im.vector.matrix.android.internal.session.room.send.LocalEchoEventFactory
import im.vector.matrix.android.internal.session.sync.ReadReceiptHandler
import im.vector.matrix.android.internal.session.sync.RoomFullyReadHandler
import im.vector.matrix.android.internal.task.Task
import im.vector.matrix.android.internal.util.awaitTransaction
import io.realm.Realm
import timber.log.Timber
import javax.inject.Inject

internal interface SetReadMarkersTask : Task<SetReadMarkersTask.Params, Unit> {

    data class Params(
            val roomId: String,
            val markAllAsRead: Boolean = false,
            val fullyReadEventId: String? = null,
            val readReceiptEventId: String? = null
    )
}

private const val READ_MARKER = "m.fully_read"
private const val READ_RECEIPT = "m.read"

internal class DefaultSetReadMarkersTask @Inject constructor(private val roomAPI: RoomAPI,
                                                             private val monarchy: Monarchy,
                                                             private val roomFullyReadHandler: RoomFullyReadHandler,
                                                             private val readReceiptHandler: ReadReceiptHandler,
                                                             @UserId private val userId: String)
    : SetReadMarkersTask {

    override suspend fun execute(params: SetReadMarkersTask.Params) {
        val markers = HashMap<String, String>()
        val fullyReadEventId: String?
        val readReceiptEventId: String?

        Timber.v("Execute set read marker with params: $params")
        if (params.markAllAsRead) {
            val latestSyncedEventId = Realm.getInstance(monarchy.realmConfiguration).use { realm ->
                TimelineEventEntity.latestEvent(realm, roomId = params.roomId, includesSending = false)?.eventId
            }
            fullyReadEventId = latestSyncedEventId
            readReceiptEventId = latestSyncedEventId
        } else {
            fullyReadEventId = params.fullyReadEventId
            readReceiptEventId = params.readReceiptEventId
        }

        if (fullyReadEventId != null && isReadMarkerMoreRecent(params.roomId, fullyReadEventId)) {
            if (LocalEchoEventFactory.isLocalEchoId(fullyReadEventId)) {
                Timber.w("Can't set read marker for local event $fullyReadEventId")
            } else {
                markers[READ_MARKER] = fullyReadEventId
            }
        }

        if (readReceiptEventId != null
            && !isEventRead(monarchy, userId, params.roomId, readReceiptEventId)) {
            if (LocalEchoEventFactory.isLocalEchoId(readReceiptEventId)) {
                Timber.w("Can't set read receipt for local event $readReceiptEventId")
            } else {
                markers[READ_RECEIPT] = readReceiptEventId
            }
        }
        if (markers.isEmpty()) {
            return
        }
        updateDatabase(params.roomId, markers)
        executeRequest<Unit> {
            apiCall = roomAPI.sendReadMarker(params.roomId, markers)
        }
    }

    private suspend fun updateDatabase(roomId: String, markers: HashMap<String, String>) {
        monarchy.awaitTransaction { realm ->
            val readMarkerId = markers[READ_MARKER]
            val readReceiptId = markers[READ_RECEIPT]
            if (readMarkerId != null) {
                roomFullyReadHandler.handle(realm, roomId, FullyReadContent(readMarkerId))
            }
            if (readReceiptId != null) {
                val readReceiptContent = ReadReceiptHandler.createContent(userId, readReceiptId)
                readReceiptHandler.handle(realm, roomId, readReceiptContent, false)
                val isLatestReceived = TimelineEventEntity.latestEvent(realm, roomId = roomId, includesSending = false)?.eventId == readReceiptId
                if (isLatestReceived) {
                    val roomSummary = RoomSummaryEntity.where(realm, roomId).findFirst()
                                      ?: return@awaitTransaction
                    roomSummary.notificationCount = 0
                    roomSummary.highlightCount = 0
                    roomSummary.hasUnreadMessages = false
                }
            }
        }
    }

    private fun isReadMarkerMoreRecent(roomId: String, newReadMarkerId: String): Boolean {
        return Realm.getInstance(monarchy.realmConfiguration).use { realm ->
            val currentReadMarkerId = ReadMarkerEntity.where(realm, roomId = roomId).findFirst()?.eventId
                                      ?: return true
            val readMarkerEvent = TimelineEventEntity.where(realm, roomId = roomId, eventId = currentReadMarkerId).findFirst()
            val newReadMarkerEvent = TimelineEventEntity.where(realm, roomId = roomId, eventId = newReadMarkerId).findFirst()
            val currentReadMarkerIndex = readMarkerEvent?.root?.displayIndex ?: Int.MAX_VALUE
            val newReadMarkerIndex = newReadMarkerEvent?.root?.displayIndex ?: Int.MIN_VALUE
            newReadMarkerIndex > currentReadMarkerIndex
        }
    }
}
