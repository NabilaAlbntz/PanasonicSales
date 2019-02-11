package pogumedia.panasonic.sales.db.dao

import android.arch.persistence.db.SimpleSQLiteQuery
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import pogumedia.panasonic.sales.db.entity.StoreSurvey
import android.arch.persistence.db.SupportSQLiteQuery
import android.arch.persistence.room.RawQuery
import android.arch.persistence.room.Delete


@Dao
interface StoreSurveyDao {

    @Query("SELECT * from store_survey where status=:status")
    fun getAllByStatus(status: Int): List<StoreSurvey>

    @Query("SELECT * from store_survey where status=:status ORDER BY idOffline desc")
    fun getAllByStatusOffline(status: Int): List<StoreSurvey>

    @Query("SELECT * from store_survey")
    fun getAll(): List<StoreSurvey>

    @Query("SELECT * from store_survey where status = 1")
    fun getAllOffline(): List<StoreSurvey>

    @Insert(onConflict = REPLACE)
    fun insert(item: StoreSurvey)

    @Query("DELETE from store_survey where status=0")
    fun deleteAllByStatus()

    @Query("DELETE from store_survey")
    fun deleteAll()

    @Query("SELECT * from store_survey where idOnline= :idOnline")
    fun getSurveyByIdOnline(idOnline: Int?): StoreSurvey

    @Query("SELECT COUNT(*) FROM store_survey WHERE idOnline = :idOnline")
    fun isSurveyExistByIdOnline(idOnline: Int?): Int

    @Query("SELECT * FROM store_survey ORDER BY idOffline DESC LIMIT 1")
    fun getMaxIdValue(): Int

    @Query("select * from store_survey inner join store on store_survey.idStoreOffline = store.idOffline  WHERE nama LIKE :query")
    fun test(query: String): List<StoreSurvey>

    @Delete
    fun deleteSurveyHistory(item: StoreSurvey)


}