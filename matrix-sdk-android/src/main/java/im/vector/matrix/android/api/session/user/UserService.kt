/*
 * Copyright 2019 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.vector.matrix.android.api.session.user

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import im.vector.matrix.android.api.MatrixCallback
import im.vector.matrix.android.api.session.user.model.User
import im.vector.matrix.android.api.util.Cancelable
import im.vector.matrix.android.api.util.Optional

/**
 * This interface defines methods to get users. It's implemented at the session level.
 */
interface UserService {

    /**
     * Get a user from a userId
     * @param userId the userId to look for.
     * @return a user with userId or null
     */
    fun getUser(userId: String): User?

    /**
     * Search list of users on server directory.
     * @param search the searched term
     * @param limit the max number of users to return
     * @param excludedUserIds the user ids to filter from the search
     * @param callback the async callback
     * @return Cancelable
     */
    fun searchUsersDirectory(search: String, limit: Int, excludedUserIds: Set<String>, callback: MatrixCallback<List<User>>): Cancelable

    /**
     * Observe a live user from a userId
     * @param userId the userId to look for.
     * @return a LiveData of user with userId
     */
    fun liveUser(userId: String): LiveData<Optional<User>>

    /**
     * Observe a live list of users sorted alphabetically
     * @return a Livedata of users
     */
    fun liveUsers(): LiveData<List<User>>

    /**
     * Observe a live [PagedList] of users sorted alphabetically. You can filter the users.
     * @param filter the filter. It will look into userId and displayName.
     * @return a Livedata of users
     */
    fun livePagedUsers(filter: String? = null): LiveData<PagedList<User>>
}
