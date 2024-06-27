package com.example.squiggleheros.utils

import android.content.Context
import android.content.SharedPreferences

object PreferenceManager {

    private const val PREFS_NAME = "com.example.squiggleheros.prefs"
    private const val FAVORITES_KEY = "favorites"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveFavorites(context: Context, favorites: Set<String>) {
        val prefs = getPreferences(context)
        with(prefs.edit()) {
            putStringSet(FAVORITES_KEY, favorites)
            apply()
        }
    }

    fun loadFavorites(context: Context): Set<String> {
        val prefs = getPreferences(context)
        return prefs.getStringSet(FAVORITES_KEY, emptySet()) ?: emptySet()
    }
}