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

package im.vector.matrix.android.internal.network

import android.content.Context
import android.text.TextUtils
import im.vector.matrix.android.BuildConfig
import im.vector.matrix.android.internal.di.MatrixScope
import timber.log.Timber
import javax.inject.Inject

@MatrixScope
internal class UserAgentHolder @Inject constructor(private val context: Context) {

    var userAgent: String = ""
        private set

    init {
        setApplicationFlavor("NoFlavor")
    }

    /**
     * Create an user agent with the application version.
     * Ex: RiotX/1.0.0 (Linux; U; Android 6.0.1; SM-A510F Build/MMB29; Flavour GPlay; MatrixAndroidSDK_X 1.0)
     *
     * @param flavorDescription the flavor description
     */
    fun setApplicationFlavor(flavorDescription: String) {
        var appName = ""
        var appVersion = ""

        try {
            val appPackageName = context.applicationContext.packageName
            val pm = context.packageManager
            val appInfo = pm.getApplicationInfo(appPackageName, 0)
            appName = pm.getApplicationLabel(appInfo).toString()

            val pkgInfo = pm.getPackageInfo(context.applicationContext.packageName, 0)
            appVersion = pkgInfo.versionName

            // Use appPackageName instead of appName if appName contains any non-ASCII character
            if (!appName.matches("\\A\\p{ASCII}*\\z".toRegex())) {
                appName = appPackageName
            }
        } catch (e: Exception) {
            Timber.e(e, "## initUserAgent() : failed")
        }

        var systemUserAgent = System.getProperty("http.agent")

        // cannot retrieve the application version
        if (TextUtils.isEmpty(appName) || TextUtils.isEmpty(appVersion)) {
            if (null == systemUserAgent) {
                userAgent = "Java" + System.getProperty("java.version")
            }
            return
        }

        // if there is no user agent or cannot parse it
        if (null == systemUserAgent || systemUserAgent.lastIndexOf(")") == -1 || !systemUserAgent.contains("(")) {
            userAgent = (appName + "/" + appVersion + " ( Flavour " + flavorDescription
                    + "; MatrixAndroidSDK_X " + BuildConfig.VERSION_NAME + ")")
        } else {
            // update
            userAgent = appName + "/" + appVersion + " " +
                    systemUserAgent.substring(systemUserAgent.indexOf("("), systemUserAgent.lastIndexOf(")") - 1) +
                    "; Flavour " + flavorDescription +
                    "; MatrixAndroidSDK_X " + BuildConfig.VERSION_NAME + ")"
        }
    }
}
