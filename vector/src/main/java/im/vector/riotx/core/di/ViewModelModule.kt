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

package im.vector.riotx.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import im.vector.riotx.core.platform.ConfigurationViewModel
import im.vector.riotx.features.crypto.keysbackup.restore.KeysBackupRestoreFromKeyViewModel
import im.vector.riotx.features.crypto.keysbackup.restore.KeysBackupRestoreFromPassphraseViewModel
import im.vector.riotx.features.crypto.keysbackup.restore.KeysBackupRestoreSharedViewModel
import im.vector.riotx.features.crypto.keysbackup.setup.KeysBackupSetupSharedViewModel
import im.vector.riotx.features.crypto.verification.SasVerificationViewModel
import im.vector.riotx.features.home.HomeNavigationViewModel
import im.vector.riotx.features.home.createdirect.CreateDirectRoomNavigationViewModel
import im.vector.riotx.features.reactions.EmojiChooserViewModel
import im.vector.riotx.features.roomdirectory.RoomDirectoryNavigationViewModel
import im.vector.riotx.features.workers.signout.SignOutViewModel

@Module
interface ViewModelModule {

    /**
     * ViewModels with @IntoMap will be injected by this factory
     */
    @Binds
    fun bindViewModelFactory(factory: VectorViewModelFactory): ViewModelProvider.Factory

    /**
     *  Below are bindings for the androidx view models (which extend ViewModel). Will be converted to MvRx ViewModel in the future.
     */

    @Binds
    @IntoMap
    @ViewModelKey(SignOutViewModel::class)
    fun bindSignOutViewModel(viewModel: SignOutViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EmojiChooserViewModel::class)
    fun bindEmojiChooserViewModel(viewModel: EmojiChooserViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SasVerificationViewModel::class)
    fun bindSasVerificationViewModel(viewModel: SasVerificationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(KeysBackupRestoreFromKeyViewModel::class)
    fun bindKeysBackupRestoreFromKeyViewModel(viewModel: KeysBackupRestoreFromKeyViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(KeysBackupRestoreSharedViewModel::class)
    fun bindKeysBackupRestoreSharedViewModel(viewModel: KeysBackupRestoreSharedViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(KeysBackupRestoreFromPassphraseViewModel::class)
    fun bindKeysBackupRestoreFromPassphraseViewModel(viewModel: KeysBackupRestoreFromPassphraseViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RoomDirectoryNavigationViewModel::class)
    fun bindRoomDirectoryNavigationViewModel(viewModel: RoomDirectoryNavigationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeNavigationViewModel::class)
    fun bindHomeNavigationViewModel(viewModel: HomeNavigationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(KeysBackupSetupSharedViewModel::class)
    fun bindKeysBackupSetupSharedViewModel(viewModel: KeysBackupSetupSharedViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ConfigurationViewModel::class)
    fun bindConfigurationViewModel(viewModel: ConfigurationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CreateDirectRoomNavigationViewModel::class)
    fun bindCreateDirectRoomNavigationViewModel(viewModel: CreateDirectRoomNavigationViewModel): ViewModel
}
