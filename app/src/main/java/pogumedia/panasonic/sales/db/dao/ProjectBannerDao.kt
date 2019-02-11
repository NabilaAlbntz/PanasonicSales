package pogumedia.panasonic.sales.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import pogumedia.panasonic.sales.db.entity.ProjectBanner
import pogumedia.panasonic.sales.db.entity.Province
import pogumedia.panasonic.sales.db.entity.User

@Dao
interface ProjectBannerDao {

    @Query("SELECT * from projectBanner")
    fun getAll(): List<ProjectBanner>

    @Insert(onConflict = REPLACE)
    fun insert(item : ProjectBanner)
    @Query("DELETE from projectbanner")
    fun deleteAll()

    @Query("SELECT * FROM projectbanner ORDER BY id DESC LIMIT 1")
    fun getLastData() : ProjectBanner


}