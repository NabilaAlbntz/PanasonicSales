package pogumedia.panasonic.sales.di

import android.app.Application
import dagger.Provides
import javax.inject.Singleton
import android.arch.persistence.room.Room
import android.content.Context
import pogumedia.panasonic.sales.db.DB
import pogumedia.panasonic.sales.db.dao.*
import dagger.Binds
import dagger.Module


@Module
abstract class RoomModule {

    @Module
    companion object {

        @JvmStatic
        @Singleton
        @Provides
        fun provideMyDatabase(context: Context): DB {
            return Room.databaseBuilder(context, DB::class.java, "panasonic-sales-db").build()
        }

        @JvmStatic
        @Singleton
        @Provides
        internal fun providesUserDao(demoDatabase: DB): UserDao {
            return demoDatabase.userDao()
        }

        @JvmStatic
        @Singleton
        @Provides
        internal fun providesStoreDao(demoDatabase: DB): StoreDao {
            return demoDatabase.storeDao()
        }

        @JvmStatic
        @Singleton
        @Provides
        internal fun providesProvinceDao(demoDatabase: DB): ProvinceDao {
            return demoDatabase.provinceDao()
        }

        @JvmStatic
        @Singleton
        @Provides
        internal fun providesCityDao(demoDatabase: DB): CityDao {
            return demoDatabase.cityDao()
        }

        @JvmStatic
        @Singleton
        @Provides
        internal fun providesProductDao(demoDatabase: DB): ProductDao {
            return demoDatabase.productDao()
        }

        @JvmStatic
        @Singleton
        @Provides
        internal fun providesProjectbannerDao(demoDatabase: DB): ProjectBannerDao {
            return demoDatabase.projectBannerDao()
        }

        @JvmStatic
        @Singleton
        @Provides
        internal fun providesTypeRequestDao(demoDatabase: DB): TypeRequestDao {
            return demoDatabase.typeRequestDao()
        }

        @JvmStatic
        @Singleton
        @Provides
        internal fun providesSignBoardDao(demoDatabase: DB): SignBoardDao {
            return demoDatabase.signBoardDao()
        }

        @JvmStatic
        @Singleton
        @Provides
        internal fun providesSurveydDao(demoDatabase: DB): StoreSurveyDao {
            return demoDatabase.storeSurveyDao()
        }

    }


}