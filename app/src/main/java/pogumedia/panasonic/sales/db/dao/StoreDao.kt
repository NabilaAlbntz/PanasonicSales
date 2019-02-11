package pogumedia.panasonic.sales.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import pogumedia.panasonic.sales.db.entity.Store
import pogumedia.panasonic.sales.db.entity.User

@Dao
interface StoreDao {

    @Query("SELECT * from store")
    fun getAll(): List<Store>

    @Query("SELECT * from store where status = 0 or status = 1")
    fun getAllHome(): List<Store>

    @Query("SELECT * from store where status = 1")
    fun getAllOffline(): List<Store>

    @Query("SELECT * from store where idOnline= :idOnline")
    fun getStoreByIdOnline(idOnline: Int?): Store

    @Query("SELECT * from store where idOffline= :idOffline")
    fun getStoreByIdOffline(idOffline: Int?): Store

    @Query("SELECT COUNT(*) FROM store where idOffline= :idOffline")
    fun isStoreByIdOfflineExist(idOffline: Int?): Int


    @Query("SELECT COUNT(*) FROM store WHERE idOnline = :idOnline")
    fun isStoreExistByIdOnline(idOnline: Int?): Int

    @Insert(onConflict = REPLACE)
    fun insert(store: Store)

    @Insert(onConflict = REPLACE)
    fun inserts(store: List<Store>)

    @Query("DELETE from store where status=0")
    fun deleteAllByStatus()

    @Query("DELETE from store")
    fun deleteAll()

    @Query("SELECT * FROM store ORDER BY idOffline DESC LIMIT 1")
    fun getMaxIdValue(): Int

    @Query("SELECT * FROM store WHERE status != 3 and nama LIKE :query")
    fun getAllByQuery(query: String): List<Store>


}