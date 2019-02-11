package pogumedia.panasonic.sales.db.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "store")
data class Store(

        @PrimaryKey
        var idOffline: Int = 0,

        @field:SerializedName("id")
        var idOnline: Int=0,

        @field:SerializedName("id_users")
        var idUsers: Int=0,

        @field:SerializedName("address")
        var address: String? = null,

        @field:SerializedName("telp")
        var telp: String? = null,

        @field:SerializedName("nama")
        var nama: String? = null,

        @field:SerializedName("nama_provinsi")
        var namaProvinsi: String? = null,

        @field:SerializedName("id_provinsi")
        var idProvinsi: Int = 0,

        @field:SerializedName("latitude")
        var latitude: String? = null,

        @field:SerializedName("nama_kabupaten")
        var namaKabupaten: String? = null,

        @field:SerializedName("pic")
        var pic: String? = null,

        @field:SerializedName("id_kabupaten")
        var idKabupaten: Int = 0,

        @field:SerializedName("nama_product")
        var namaProduct: String? = null,

        @field:SerializedName("longitude")
        var longitude: String? = null,

        @field:SerializedName("created_at")
        var createdAt: String? = null,

        @field:SerializedName("is_reject")
        var isReject: Boolean = false,

        @field:SerializedName("is_delete")
        var isDelete: Boolean = false,

        var status: Int = 0
)