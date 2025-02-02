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

package im.vector.riotx.features.login

import im.vector.matrix.android.api.auth.data.Credentials

sealed class LoginActions {

    data class UpdateHomeServer(val homeServerUrl: String) : LoginActions()
    data class Login(val login: String, val password: String) : LoginActions()
    data class SsoLoginSuccess(val credentials: Credentials) : LoginActions()
    data class NavigateTo(val target: LoginActivity.Navigation) : LoginActions()
    data class InitWith(val loginConfig: LoginConfig) : LoginActions()
}
