/*
 * Copyright 2018 New Vector Ltd
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

package im.vector.matrix.android.api.extensions

import im.vector.matrix.android.api.comparators.DatedObjectComparators
import im.vector.matrix.android.internal.crypto.model.MXDeviceInfo
import im.vector.matrix.android.internal.crypto.model.rest.DeviceInfo
import java.util.Collections

/* ==========================================================================================
 * MXDeviceInfo
 * ========================================================================================== */

fun MXDeviceInfo.getFingerprintHumanReadable() = fingerprint()
        ?.chunked(4)
        ?.joinToString(separator = " ")

fun List<DeviceInfo>.sortByLastSeen() {
    Collections.sort(this, DatedObjectComparators.descComparator)
}
