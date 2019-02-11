package pogumedia.panasonic.sales.di

import pogumedia.panasonic.sales.ui.actvity.forgot.ForgotActivity
import pogumedia.panasonic.sales.ui.actvity.forgot.ForgotModule
import pogumedia.panasonic.sales.ui.actvity.login.LoginActivity
import pogumedia.panasonic.sales.ui.actvity.login.LoginModule
import pogumedia.panasonic.sales.ui.actvity.main.MainActivity
import pogumedia.panasonic.sales.ui.actvity.main.MainModule
import pogumedia.panasonic.sales.ui.actvity.store.item.StoreItemActivity
import pogumedia.panasonic.sales.ui.actvity.store.item.StoreItemModule
import pogumedia.panasonic.sales.ui.actvity.store.location.StoreLocationActivity
import pogumedia.panasonic.sales.ui.actvity.store.location.StoreLocationModule
import pogumedia.panasonic.sales.ui.actvity.store.search.StoreSearchActivity
import pogumedia.panasonic.sales.ui.actvity.store.search.StoreSearchModule
import pogumedia.panasonic.sales.ui.actvity.store.survey.item.StoreSurveyItemActivity
import pogumedia.panasonic.sales.ui.actvity.store.survey.item.StoreSurveyItemModule
import pogumedia.panasonic.sales.ui.actvity.store.survey.history.search.StoreSurveyHistorySearchActivity
import pogumedia.panasonic.sales.ui.actvity.store.survey.history.search.StoreSurveyHistorySearchModule
import pogumedia.panasonic.sales.ui.actvity.store.visit.StoreVisitActivity
import pogumedia.panasonic.sales.ui.actvity.store.visit.StoreVisitModule
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [(MainModule::class)])
    abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [(LoginModule::class)])
    abstract fun bindLoginActivity(): LoginActivity

    @ContributesAndroidInjector(modules = [(ForgotModule::class)])
    abstract fun bindForgotActivity(): ForgotActivity

    @ContributesAndroidInjector(modules = [(StoreSearchModule::class)])
    abstract fun bindStoreSearchActivity(): StoreSearchActivity

    @ContributesAndroidInjector(modules = [(StoreItemModule::class)])
    abstract fun bindStoreItemActivity(): StoreItemActivity

    @ContributesAndroidInjector(modules = [(StoreVisitModule::class)])
    abstract fun bindStoreVisitActivity(): StoreVisitActivity

    @ContributesAndroidInjector(modules = [(StoreLocationModule::class)])
    abstract fun bindStoreLocationActivity(): StoreLocationActivity

    @ContributesAndroidInjector(modules = [(StoreSurveyItemModule::class)])
    abstract fun bindStoreSurveyActivity(): StoreSurveyItemActivity

    @ContributesAndroidInjector(modules = [(StoreSurveyHistorySearchModule::class)])
    abstract fun bindStoreSurveySearchActivity(): StoreSurveyHistorySearchActivity

}