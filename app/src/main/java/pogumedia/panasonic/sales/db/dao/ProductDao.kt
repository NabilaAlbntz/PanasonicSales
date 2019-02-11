package pogumedia.panasonic.sales.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import pogumedia.panasonic.sales.db.entity.Product
import pogumedia.panasonic.sales.db.entity.Province
import pogumedia.panasonic.sales.db.entity.User

@Dao
interface ProductDao {

    @Query("SELECT * from product")
    fun getAll(): List<Product>

    @Insert(onConflict = REPLACE)
    fun insert(item : Product)

    @Query("DELETE from product")
    fun deleteAll()

    @Query("SELECT * FROM product ORDER BY id DESC LIMIT 1")
    fun getLastData() : Product

}