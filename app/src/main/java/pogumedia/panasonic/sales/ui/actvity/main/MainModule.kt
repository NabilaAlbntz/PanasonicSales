package pogumedia.panasonic.sales.ui.actvity.main

import com.google.gson.Gson
import pogumedia.panasonic.sales.db.SessionManager
import pogumedia.panasonic.sales.db.dao.*
import pogumedia.panasonic.sales.service.api.ApiInterface
import pogumedia.panasonic.sales.service.api.MasterApi
import pogumedia.panasonic.sales.ui.actvity.home.HomeFragment
import pogumedia.panasonic.sales.ui.actvity.home.HomeModule
import pogumedia.panasonic.sales.ui.actvity.profile.ProfileFragment
import pogumedia.panasonic.sales.ui.actvity.profile.ProfileModule
import pogumedia.panasonic.sales.ui.actvity.store.survey.history.StoreSurveyHistoryFragment
import pogumedia.panasonic.sales.ui.actvity.store.survey.history.StoreSurveyHistoryModule
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainModule {

    @ContributesAndroidInjector(modules = [(HomeModule::class)])
    abstract fun provideHomeFragment(): HomeFragment

    @ContributesAndroidInjector(modules = [(ProfileModule::class)])
    abstract fun provideProfileFragment(): ProfileFragment

    @ContributesAndroidInjector(modules = [(StoreSurveyHistoryModule::class)])
    abstract fun provideHistoryFragment(): StoreSurveyHistoryFragment

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideMasterApi(
                apiInterface: ApiInterface,
                gson: Gson,
                provinceDao: ProvinceDao,
                productDao: ProductDao,
                cityDao: CityDao,
                projectBannerDao: ProjectBannerDao,
                signBoardDao: SignBoardDao,
                typeRequestDao: TypeRequestDao,
                sessionManager: SessionManager): MasterApi {
            return MasterApi(provinceDao, cityDao, gson, productDao, projectBannerDao,
                    signBoardDao, typeRequestDao, apiInterface,sessionManager)
        }


    }

}