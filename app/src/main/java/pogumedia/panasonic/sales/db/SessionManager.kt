package pogumedia.panasonic.sales.db

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

/**
 * Created by crocodicstudio on 4/24/18.
 */

@SuppressLint("CommitPrefEdits")
class SessionManager(context: Context) {

    var prefs: SharedPreferences
    var editor: SharedPreferences.Editor
    val PRIVATE_MODE = 0
    val PREF_NAME = "PanasonicSales"
    val IS_LOGIN = "isLoggedIn"
    val KEY_ID = "idOnline"


    init {
        prefs = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = prefs.edit()
    }

    fun createLoginSession(id: Int, isLogin: Boolean) {
        editor.putBoolean(IS_LOGIN, isLogin)
        editor.putInt(KEY_ID, id)
        editor.commit()
    }


    fun getId(): Int {
        return prefs.getInt(KEY_ID, 0)
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(IS_LOGIN, false)
    }


}