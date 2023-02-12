package map.mine.audiologs.retrofit

import android.content.Context
import android.content.SharedPreferences
import map.mine.audiologs.R

class SessionManager (context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val USERNAME = "username"
    }

    fun saveUserName(username: String){
        val editor = prefs.edit()
        editor.putString(USERNAME, username)
        editor.apply()
    }

    /**
     * Function to save auth token
     */
    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    /**
     * Function to fetch auth token
     */
    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun fetchUserName(): String? {
        return prefs.getString(USERNAME, null)
    }
}