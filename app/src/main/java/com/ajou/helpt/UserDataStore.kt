package com.ajou.helpt

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name= "userData")

class UserDataStore() {
    private val context = Application.context()
    private val dataStore : DataStore<Preferences> = context.dataStore

    private object PreferencesKeys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val HAS_TICKET = booleanPreferencesKey("has_ticket")
        val USER_NAME = stringPreferencesKey("user_name")
        val KAKAO_ID = stringPreferencesKey("kakao_id")
    }

    suspend fun saveAccessToken(token : String) {
        withContext(Dispatchers.IO){
            dataStore.edit { pref ->
                pref[PreferencesKeys.ACCESS_TOKEN] = token
            }
        }
    }

    suspend fun getAccessToken():String? {
        return withContext(Dispatchers.IO) {
            dataStore.data.first()[PreferencesKeys.ACCESS_TOKEN]
        }
    }

    suspend fun saveRefreshToken(token : String) {
        withContext(Dispatchers.IO){
            dataStore.edit { pref ->
                pref[PreferencesKeys.REFRESH_TOKEN] = token
            }
        }
    }

    suspend fun getRefreshToken():String? {
        return withContext(Dispatchers.IO) {
            dataStore.data.first()[PreferencesKeys.REFRESH_TOKEN]
        }
    }

    suspend fun saveHasTicket(hasTicket : Boolean) {
        withContext(Dispatchers.IO){
            dataStore.edit { pref ->
                pref[PreferencesKeys.HAS_TICKET] = hasTicket
            }
        }
    }

    suspend fun getHasTicket() : Boolean {
        return withContext(Dispatchers.IO) {
            dataStore.data.first()[PreferencesKeys.HAS_TICKET] ?: false
        }
    }

    suspend fun saveUserName(name : String) {
        withContext(Dispatchers.IO){
            dataStore.edit { pref ->
                pref[PreferencesKeys.USER_NAME] = name
            }
        }
    }

    suspend fun getUserName():String? {
        return withContext(Dispatchers.IO) {
            dataStore.data.first()[PreferencesKeys.USER_NAME]
        }
    }

    suspend fun saveKakaoId(id:String) {
        withContext(Dispatchers.IO){
            dataStore.edit { pref ->
                pref[PreferencesKeys.KAKAO_ID] = id
            }
        }
    }

    suspend fun getKakaoId():String? {
        return withContext(Dispatchers.IO) {
            dataStore.data.first()[PreferencesKeys.KAKAO_ID]
        }
    }

    suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            dataStore.edit { pref ->
                pref.clear()
            }
        }
    }

}