package pogumedia.panasonic.sales.db.entity


import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class City(

        @PrimaryKey
        @field:SerializedName("id")
        var id: Int= 0 ,

        @field:SerializedName("id_provinsi")
        var idProvinsi: Int? = null,

        @field:SerializedName("nama_kabupaten")
        var namaKabupaten: String? = null

) {
    override fun toString(): String {
        return namaKabupaten.toString()
    }
}