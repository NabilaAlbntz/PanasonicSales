package pogumedia.panasonic.sales.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import pogumedia.panasonic.sales.db.entity.ProjectBanner
import pogumedia.panasonic.sales.db.entity.Province
import pogumedia.panasonic.sales.db.entity.TypeRequest
import pogumedia.panasonic.sales.db.entity.User

@Dao
interface TypeRequestDao {

    @Query("SELECT * from typeRequest")
    fun getAll(): List<TypeRequest>

    @Insert(onConflict = REPLACE)
    fun insert(item : TypeRequest)

    @Query("DELETE from typerequest")
    fun deleteAll()

    @Query("SELECT * FROM typerequest ORDER BY id DESC LIMIT 1")
    fun getLastData() : TypeRequest
}