package pogumedia.panasonic.sales.ui.actvity.store.survey.history.status

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import pogumedia.panasonic.sales.db.SessionManager
import pogumedia.panasonic.sales.db.dao.StoreDao
import pogumedia.panasonic.sales.db.dao.StoreSurveyDao
import pogumedia.panasonic.sales.di.annotation.ViewModelKey
import pogumedia.panasonic.sales.receiver.SyncSurveyDraft
import pogumedia.panasonic.sales.service.api.ApiInterface
import pogumedia.panasonic.sales.ui.base.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
abstract class StoreSurveyHistoryStatusModule {

    @Binds
    @IntoMap
    @ViewModelKey(StoreSurveyHistoryStatusViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: StoreSurveyHistoryStatusViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory



}