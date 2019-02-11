package pogumedia.panasonic.sales.db.entity


import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "user")
data class User(

        @PrimaryKey
        @field:SerializedName("id")
        var id: Int? = null,

        @field:SerializedName("user_kode")
        var userKode: String? = null,

        @field:SerializedName("user_role")
        var userRole: String? = null,

        @field:SerializedName("user_nama")
        var userNama: String? = null,

        @field:SerializedName("user_email")
        var userEmail: String? = null,

        @field:SerializedName("user_telp")
        var userTelp: String? = null,

        var password: String =""


)