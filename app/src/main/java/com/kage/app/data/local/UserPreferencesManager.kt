package com.kage.app.data.local

import android.content.Context
import android.content.SharedPreferences

class UserPreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("kage_user_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_FAVORITES = "favorite_ids"
        private const val KEY_BROKEN = "broken_ids"
    }

    fun getFavoriteIds(): Set<String> = prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()

    fun isFavorite(id: String): Boolean = getFavoriteIds().contains(id)

    fun toggleFavorite(id: String) {
        val current = getFavoriteIds().toMutableSet()
        if (!current.add(id)) current.remove(id)
        prefs.edit().putStringSet(KEY_FAVORITES, current).apply()
    }

    fun getBrokenIds(): Set<String> = prefs.getStringSet(KEY_BROKEN, emptySet()) ?: emptySet()

    fun isBroken(id: String): Boolean = getBrokenIds().contains(id)

    fun toggleBroken(id: String) {
        val current = getBrokenIds().toMutableSet()
        if (!current.add(id)) current.remove(id)
        prefs.edit().putStringSet(KEY_BROKEN, current).apply()
    }
}