package pogumedia.panasonic.sales.ui.actvity.store.item

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import pogumedia.panasonic.sales.di.annotation.ViewModelKey
import pogumedia.panasonic.sales.ui.base.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class StoreItemModule {

    @Binds
    @IntoMap
    @ViewModelKey(StoreItemViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: StoreItemViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory
}