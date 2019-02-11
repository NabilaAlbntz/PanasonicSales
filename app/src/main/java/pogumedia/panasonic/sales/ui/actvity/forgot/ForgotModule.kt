package pogumedia.panasonic.sales.ui.actvity.forgot

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import pogumedia.panasonic.sales.di.annotation.ViewModelKey
import pogumedia.panasonic.sales.ui.base.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ForgotModule {

    @Binds
    @IntoMap
    @ViewModelKey(ForgotViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: ForgotViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory
}