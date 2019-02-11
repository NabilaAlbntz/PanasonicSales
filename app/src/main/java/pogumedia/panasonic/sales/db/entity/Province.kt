package pogumedia.panasonic.sales.db.entity


import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Province(

        @PrimaryKey
        @field:SerializedName("id")
        var id: Int=0,

        @field:SerializedName("nama_provinsi")
        var namaProvinsi: String? = null

) {
    override fun toString(): String {
        return namaProvinsi.toString()
    }
}