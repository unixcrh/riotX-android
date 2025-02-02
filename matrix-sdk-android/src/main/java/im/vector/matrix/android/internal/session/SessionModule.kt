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

package im.vector.matrix.android.internal.session

import android.content.Context
import com.zhuinden.monarchy.Monarchy
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import im.vector.matrix.android.api.auth.data.Credentials
import im.vector.matrix.android.api.auth.data.HomeServerConnectionConfig
import im.vector.matrix.android.api.auth.data.SessionParams
import im.vector.matrix.android.api.session.InitialSyncProgressService
import im.vector.matrix.android.api.session.Session
import im.vector.matrix.android.api.session.homeserver.HomeServerCapabilitiesService
import im.vector.matrix.android.api.session.securestorage.SecureStorageService
import im.vector.matrix.android.internal.database.LiveEntityObserver
import im.vector.matrix.android.internal.database.SessionRealmConfigurationFactory
import im.vector.matrix.android.internal.di.*
import im.vector.matrix.android.internal.network.AccessTokenInterceptor
import im.vector.matrix.android.internal.network.RetrofitFactory
import im.vector.matrix.android.internal.network.interceptors.CurlLoggingInterceptor
import im.vector.matrix.android.internal.session.group.GroupSummaryUpdater
import im.vector.matrix.android.internal.session.homeserver.DefaultHomeServerCapabilitiesService
import im.vector.matrix.android.internal.session.room.EventRelationsAggregationUpdater
import im.vector.matrix.android.internal.session.room.create.RoomCreateEventLiveObserver
import im.vector.matrix.android.internal.session.room.prune.EventsPruner
import im.vector.matrix.android.internal.session.room.tombstone.RoomTombstoneEventLiveObserver
import im.vector.matrix.android.internal.session.securestorage.DefaultSecureStorageService
import im.vector.matrix.android.internal.util.md5
import io.realm.RealmConfiguration
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.io.File

@Module
internal abstract class SessionModule {

    @Module
    companion object {

        internal const val DB_ALIAS_PREFIX = "session_db_"

        @JvmStatic
        @Provides
        fun providesHomeServerConnectionConfig(sessionParams: SessionParams): HomeServerConnectionConfig {
            return sessionParams.homeServerConnectionConfig
        }

        @JvmStatic
        @Provides
        fun providesCredentials(sessionParams: SessionParams): Credentials {
            return sessionParams.credentials
        }

        @JvmStatic
        @UserId
        @Provides
        fun providesUserId(credentials: Credentials): String {
            return credentials.userId
        }

        @JvmStatic
        @UserMd5
        @Provides
        fun providesUserMd5(@UserId userId: String): String {
            return userId.md5()
        }

        @JvmStatic
        @Provides
        @UserCacheDirectory
        fun providesFilesDir(@UserMd5 userMd5: String, context: Context): File {
            return File(context.filesDir, userMd5)
        }

        @JvmStatic
        @Provides
        @SessionDatabase
        @SessionScope
        fun providesRealmConfiguration(realmConfigurationFactory: SessionRealmConfigurationFactory): RealmConfiguration {
            return realmConfigurationFactory.create()
        }

        @JvmStatic
        @Provides
        @SessionScope
        fun providesMonarchy(@SessionDatabase
                             realmConfiguration: RealmConfiguration): Monarchy {
            return Monarchy.Builder()
                    .setRealmConfiguration(realmConfiguration)
                    .build()
        }

        @JvmStatic
        @Provides
        @SessionScope
        @Authenticated
        fun providesOkHttpClient(@Unauthenticated okHttpClient: OkHttpClient,
                                 accessTokenInterceptor: AccessTokenInterceptor): OkHttpClient {
            return okHttpClient.newBuilder()
                    .apply {
                        // Remove the previous CurlLoggingInterceptor, to add it after the accessTokenInterceptor
                        val existingCurlInterceptors = interceptors().filterIsInstance<CurlLoggingInterceptor>()
                        interceptors().removeAll(existingCurlInterceptors)

                        addInterceptor(accessTokenInterceptor)

                        // Re add eventually the curl logging interceptors
                        existingCurlInterceptors.forEach {
                            addInterceptor(it)
                        }
                    }
                    .build()
        }

        @JvmStatic
        @Provides
        @SessionScope
        fun providesRetrofit(@Authenticated okHttpClient: OkHttpClient,
                             sessionParams: SessionParams,
                             retrofitFactory: RetrofitFactory): Retrofit {
            return retrofitFactory
                    .create(okHttpClient, sessionParams.homeServerConnectionConfig.homeServerUri.toString())
        }
    }

    @Binds
    abstract fun bindSession(session: DefaultSession): Session

    @Binds
    @IntoSet
    abstract fun bindGroupSummaryUpdater(groupSummaryUpdater: GroupSummaryUpdater): LiveEntityObserver

    @Binds
    @IntoSet
    abstract fun bindEventsPruner(eventsPruner: EventsPruner): LiveEntityObserver

    @Binds
    @IntoSet
    abstract fun bindEventRelationsAggregationUpdater(eventRelationsAggregationUpdater: EventRelationsAggregationUpdater): LiveEntityObserver

    @Binds
    @IntoSet
    abstract fun bindRoomTombstoneEventLiveObserver(roomTombstoneEventLiveObserver: RoomTombstoneEventLiveObserver): LiveEntityObserver

    @Binds
    @IntoSet
    abstract fun bindRoomCreateEventLiveObserver(roomCreateEventLiveObserver: RoomCreateEventLiveObserver): LiveEntityObserver

    @Binds
    abstract fun bindInitialSyncProgressService(initialSyncProgressService: DefaultInitialSyncProgressService): InitialSyncProgressService

    @Binds
    abstract fun bindSecureStorageService(secureStorageService: DefaultSecureStorageService): SecureStorageService

    @Binds
    abstract fun bindHomeServerCapabilitiesService(homeServerCapabilitiesService: DefaultHomeServerCapabilitiesService): HomeServerCapabilitiesService
}
