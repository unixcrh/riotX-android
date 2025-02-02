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

package im.vector.matrix.android.internal.session.filter

internal interface FilterRepository {

    /**
     * Return true if the filterBody has changed, or need to be sent to the server
     */
    fun storeFilter(filterBody: FilterBody, roomEventFilter: RoomEventFilter): Boolean

    /**
     * Set the filterId of this filter
     */
    fun storeFilterId(filterBody: FilterBody, filterId: String)

    /**
     * Return filter json or filter id
     */
    fun getFilter(): String

    /**
     * Return the room filter
     */
    fun getRoomFilter(): String
}
