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

package im.vector.matrix.android.api.failure

import im.vector.matrix.android.api.session.crypto.MXCryptoError
import im.vector.matrix.android.internal.auth.registration.RegistrationFlowResponse
import java.io.IOException

/**
 * This class allows to expose different kinds of error to be then handled by the application.
 * As it is a sealed class, you typically use it like that :
 * when(failure) {
 *   is NetworkConnection -> Unit
 *   is ServerError       -> Unit
 *   is Unknown           -> Unit
 * }
 */
sealed class Failure(cause: Throwable? = null) : Throwable(cause = cause) {
    data class Unknown(val throwable: Throwable? = null) : Failure(throwable)
    data class Cancelled(val throwable: Throwable? = null) : Failure(throwable)
    data class NetworkConnection(val ioException: IOException? = null) : Failure(ioException)
    data class ServerError(val error: MatrixError, val httpCode: Int) : Failure(RuntimeException(error.toString()))
    // When server send an error, but it cannot be interpreted as a MatrixError
    data class OtherServerError(val errorBody: String, val httpCode: Int) : Failure(RuntimeException(errorBody))

    data class RegistrationFlowError(val registrationFlowResponse: RegistrationFlowResponse) : Failure(RuntimeException(registrationFlowResponse.toString()))

    data class CryptoError(val error: MXCryptoError) : Failure(error)

    abstract class FeatureFailure : Failure()
}
