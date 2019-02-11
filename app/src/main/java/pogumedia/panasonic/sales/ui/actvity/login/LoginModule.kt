package pogumedia.panasonic.sales.ui.actvity.login

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import pogumedia.panasonic.sales.di.annotation.ViewModelKey
import pogumedia.panasonic.sales.ui.base.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class LoginModule {

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: LoginViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory
}