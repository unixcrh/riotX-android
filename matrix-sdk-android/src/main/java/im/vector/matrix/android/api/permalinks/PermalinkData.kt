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

package im.vector.matrix.android.api.permalinks

import android.net.Uri

/**
 * This sealed class represents all the permalink cases.
 * You don't have to instantiate yourself but should use [PermalinkParser] instead.
 */
sealed class PermalinkData {

    data class EventLink(val roomIdOrAlias: String, val eventId: String) : PermalinkData()

    data class RoomLink(val roomIdOrAlias: String) : PermalinkData()

    data class UserLink(val userId: String) : PermalinkData()

    data class GroupLink(val groupId: String) : PermalinkData()

    data class FallbackLink(val uri: Uri) : PermalinkData()
}
