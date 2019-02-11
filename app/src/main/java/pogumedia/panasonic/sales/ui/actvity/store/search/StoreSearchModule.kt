package pogumedia.panasonic.sales.ui.actvity.store.search

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import pogumedia.panasonic.sales.di.annotation.ViewModelKey
import pogumedia.panasonic.sales.ui.base.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class StoreSearchModule {

    @Binds
    @IntoMap
    @ViewModelKey(StoreSearchViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: StoreSearchViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory
}