package pogumedia.panasonic.sales.db.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "store_survey")
data class StoreSurvey(

        @PrimaryKey
        var idOffline: Int = 0,

        @field:SerializedName("id")
        var idOnline: Int = 0,

        @field:SerializedName("id_outlet")
        var idStoreOnline: Int = 0,

        var idStoreOffline: Int = 0,

        @field:SerializedName("product")
        var product: String? = null,

        @field:SerializedName("project_banner")
        var banner: String? = null,

        @field:SerializedName("signboard")
        var signBoard: String? = null,

        @field:SerializedName("ukuran_banner")
        var ukuranBanner: String? = null,

        @field:SerializedName("type_request")
        var typeRequest: String? = null,

        @field:SerializedName("foto_aktual")
        var fotoAktual: String? = null,

        @field:SerializedName("latitude")
        var locLatitude: String? = null,

        @field:SerializedName("longitude")
        var locLongitude: String? = null,

        @field:SerializedName("note")
        var note: String? = null,

        @field:SerializedName("created_at")
        var createdAt: String? = null,

        @field:SerializedName("status")
        var statusSurvey: String? = null,

        @field:SerializedName("warna")
        var warna: String = "#00C685",

        @field:SerializedName("is_in_radius")
        var inRadius: Boolean = true,

        @field:SerializedName("statusUpload")
        var status: Int = 0,

        @SerializedName("data_toko")
        @Ignore
        var store: Store = Store()
)