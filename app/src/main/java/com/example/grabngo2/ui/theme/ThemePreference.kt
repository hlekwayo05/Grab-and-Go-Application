package com.example.grabngo2.ui.theme

import android.content.Context
import android.content.SharedPreferences

object ThemePreference {
    private const val PREF_NAME = "grabngo_prefs"
    private const val KEY_THEME = "selected_theme"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveTheme(context: Context, themeChoice: String) {
        getPrefs(context).edit().putString(KEY_THEME, themeChoice).apply()
    }

    fun getTheme(context: Context): String {
        return getPrefs(context).getString(KEY_THEME, "dark") ?: "dark"
    }
}
