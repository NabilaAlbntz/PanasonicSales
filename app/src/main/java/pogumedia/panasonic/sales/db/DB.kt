package pogumedia.panasonic.sales.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import pogumedia.panasonic.sales.db.dao.*
import pogumedia.panasonic.sales.db.entity.*

@Database(entities =
[
    (User::class),
    (Store::class),
    (StoreSurvey::class),
    (Province::class),
    (City::class),
    (Product::class),
    (ProjectBanner::class),
    (TypeRequest::class),
    (SignBoard::class)
], version = 1)
abstract class DB : RoomDatabase() {


    abstract fun userDao(): UserDao
    abstract fun storeDao(): StoreDao
    abstract fun provinceDao(): ProvinceDao
    abstract fun cityDao(): CityDao
    abstract fun productDao(): ProductDao
    abstract fun signBoardDao(): SignBoardDao
    abstract fun projectBannerDao(): ProjectBannerDao
    abstract fun typeRequestDao(): TypeRequestDao
    abstract fun storeSurveyDao(): StoreSurveyDao

}