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

package im.vector.matrix.android.internal.session.sync

import im.vector.matrix.android.internal.database.model.SyncEntity
import im.vector.matrix.android.internal.di.SessionDatabase
import io.realm.Realm
import io.realm.RealmConfiguration
import javax.inject.Inject

internal class SyncTokenStore @Inject constructor(@SessionDatabase private val realmConfiguration: RealmConfiguration) {

    fun getLastToken(): String? {
        val realm = Realm.getInstance(realmConfiguration)
        val token = realm.where(SyncEntity::class.java).findFirst()?.nextBatch
        realm.close()
        return token
    }

    fun saveToken(token: String?) {
        val realm = Realm.getInstance(realmConfiguration)
        realm.executeTransaction {
            val sync = SyncEntity(token)
            it.insertOrUpdate(sync)
        }
        realm.close()
    }
}
