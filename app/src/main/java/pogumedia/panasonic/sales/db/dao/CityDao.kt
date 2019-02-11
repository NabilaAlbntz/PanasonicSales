package pogumedia.panasonic.sales.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import pogumedia.panasonic.sales.db.entity.City
import pogumedia.panasonic.sales.db.entity.Province
import pogumedia.panasonic.sales.db.entity.User

@Dao
interface CityDao {

    @Query("SELECT * from city")
    fun getAll(): List<City>

    @Query("SELECT * from city where idProvinsi=:provinceId")
    fun getAllByProvinceId(provinceId: Int): List<City>


    @Insert(onConflict = REPLACE)
    fun insert(item: City)

    @Query("DELETE from city")
    fun deleteAll()



}