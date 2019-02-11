package pogumedia.panasonic.sales.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import pogumedia.panasonic.sales.db.entity.Province
import pogumedia.panasonic.sales.db.entity.User

@Dao
interface ProvinceDao {

    @Query("SELECT * from province")
    fun getAll(): List<Province>

    @Insert(onConflict = REPLACE)
    fun insert(item : Province)
    @Query("DELETE from province")
    fun deleteAll()


}