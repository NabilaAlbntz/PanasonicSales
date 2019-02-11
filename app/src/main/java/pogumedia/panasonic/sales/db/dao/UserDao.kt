package pogumedia.panasonic.sales.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import pogumedia.panasonic.sales.db.entity.User

@Dao
interface UserDao {

    @Query("SELECT * from user")
    fun getAll(): List<User>

    @Query("SELECT * from user where id= :id")
    fun getUser(id : Int): User

    @Insert(onConflict = REPLACE)
    fun insert(user: User)

    @Query("DELETE from user")
    fun deleteAll()


}