package pogumedia.panasonic.sales.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import pogumedia.panasonic.sales.db.entity.ProjectBanner
import pogumedia.panasonic.sales.db.entity.Province
import pogumedia.panasonic.sales.db.entity.SignBoard
import pogumedia.panasonic.sales.db.entity.User

@Dao
interface SignBoardDao {

    @Query("SELECT * from signBoard")
    fun getAll(): List<SignBoard>

    @Insert(onConflict = REPLACE)
    fun insert(item : SignBoard)
    @Query("DELETE from signboard")
    fun deleteAll()

    @Query("SELECT * FROM signboard ORDER BY id DESC LIMIT 1")
    fun getLastData() : SignBoard
}