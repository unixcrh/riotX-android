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

package im.vector.riotx.features.home.room.detail.readreceipts

import com.airbnb.epoxy.TypedEpoxyController
import im.vector.matrix.android.api.session.Session
import im.vector.riotx.core.date.VectorDateFormatter
import im.vector.riotx.features.home.AvatarRenderer
import im.vector.riotx.features.home.room.detail.timeline.item.ReadReceiptData
import javax.inject.Inject

/**
 * Epoxy controller for read receipt event list
 */
class DisplayReadReceiptsController @Inject constructor(private val dateFormatter: VectorDateFormatter,
                                                        private val session: Session,
                                                        private val avatarRender: AvatarRenderer)
    : TypedEpoxyController<List<ReadReceiptData>>() {

    override fun buildModels(readReceipts: List<ReadReceiptData>) {
        readReceipts.forEach {
            val timestamp = dateFormatter.formatRelativeDateTime(it.timestamp)
            DisplayReadReceiptItem_()
                    .id(it.userId)
                    .userId(it.userId)
                    .avatarUrl(it.avatarUrl)
                    .name(it.displayName)
                    .avatarRenderer(avatarRender)
                    .timestamp(timestamp)
                    .addIf(session.myUserId != it.userId, this)
        }
    }
}
