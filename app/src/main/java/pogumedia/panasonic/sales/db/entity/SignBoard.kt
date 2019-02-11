package pogumedia.panasonic.sales.db.entity


import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class SignBoard(

        @PrimaryKey
        @field:SerializedName("id")
        var id: Int = 0,

        @field:SerializedName("name_signboard")
        var namaSignBoard: String? = null

) {
    override fun toString(): String {
        return namaSignBoard.toString()
    }
}