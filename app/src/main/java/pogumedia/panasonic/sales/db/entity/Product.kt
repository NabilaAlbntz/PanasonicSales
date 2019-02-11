package pogumedia.panasonic.sales.db.entity


import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Product(

        @PrimaryKey
        @field:SerializedName("id")
        var id: Int=0,

        @field:SerializedName("nama_product")
        var name: String? = null

) {
    override fun toString(): String {
        return name.toString()
    }
}