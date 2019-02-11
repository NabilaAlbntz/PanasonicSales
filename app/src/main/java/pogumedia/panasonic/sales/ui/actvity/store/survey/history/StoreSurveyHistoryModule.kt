package pogumedia.panasonic.sales.ui.actvity.store.survey.history

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.google.gson.Gson
import pogumedia.panasonic.sales.db.dao.*
import pogumedia.panasonic.sales.di.annotation.ViewModelKey
import pogumedia.panasonic.sales.service.api.ApiInterface
import pogumedia.panasonic.sales.service.api.MasterApi
import pogumedia.panasonic.sales.ui.actvity.home.HomeFragment
import pogumedia.panasonic.sales.ui.actvity.home.HomeModule
import pogumedia.panasonic.sales.ui.actvity.profile.ProfileFragment
import pogumedia.panasonic.sales.ui.actvity.profile.ProfileModule
import pogumedia.panasonic.sales.ui.actvity.store.survey.history.StoreSurveyHistoryFragment
import pogumedia.panasonic.sales.ui.actvity.store.survey.history.status.StoreSurveyHistoryStatusFragment
import pogumedia.panasonic.sales.ui.actvity.store.survey.history.status.StoreSurveyHistoryStatusModule
import pogumedia.panasonic.sales.ui.base.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class StoreSurveyHistoryModule {

    @ContributesAndroidInjector(modules = [(StoreSurveyHistoryStatusModule::class)])
    abstract fun provideHistoryStatusFragment(): StoreSurveyHistoryStatusFragment


}