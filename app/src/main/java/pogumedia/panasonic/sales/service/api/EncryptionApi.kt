package pogumedia.panasonic.sales.service.api

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


class EncryptionApi {

    var SCREET_KEY = "a7445917b80185164b526986ab4ac069"
    lateinit var token_time: String

    fun crypt(): String {
        token_time = currentTime.toString()
        val token_kode = SCREET_KEY + token_time + KEY_USER_AGENT
        if (token_kode.isEmpty()) {
            throw IllegalArgumentException("String to encript cannot be null or zero length")
        }

        digester!!.update(token_kode.toByteArray())
        val hash = digester!!.digest()
        val hexString = StringBuffer()
        for (i in hash.indices) {
            if (0xff and hash[i].toInt() < 0x10) {
                hexString.append("0" + Integer.toHexString(0xFF and hash[i].toInt()))
            } else {
                hexString.append(Integer.toHexString(0xFF and hash[i].toInt()))
            }
        }

        return hexString.toString()
    }

    companion object {
        val KEY_TOKEN_CODE = "token_code"
        val KEY_TOKEN_TIME = "token_time"
        var KEY_USER_AGENT = "ANDROID"

        private val currentTime: Long
            get() {
                val date = Date()
                return date.time
            }

        private var digester: MessageDigest? = null

        init {
            try {
                digester = MessageDigest.getInstance("MD5")
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }

        }
    }
}