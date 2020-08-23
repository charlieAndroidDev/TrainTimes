package com.cniekirk.traintimes.data.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.cniekirk.traintimes.model.PushPortMessageItem
import com.cniekirk.traintimes.model.ui.ServiceDetailsUiModel
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

private const val SHOW_PRICES = "show_prices"
private const val DEFAULT_PAGE = "default_page_live"
private const val FIREBASE_ID = "fb_id"
private const val TRACKED_SERVICE = "tracked_service"

class PreferenceProvider(context: Context) {

    private val appContext = context.applicationContext

    private val preferences: SharedPreferences
    get() = PreferenceManager.getDefaultSharedPreferences(appContext)

    fun getShouldShowPrices(): Boolean {
        return preferences.getBoolean(SHOW_PRICES, false)
    }

    fun getIsLiveDefaultPage(): Boolean {
        return preferences.getBoolean(DEFAULT_PAGE, false)
    }

    fun setFirebaseId(fbId: String) {
        preferences.edit().putString(FIREBASE_ID, fbId).apply()
    }

    fun getFirebaseId(): String {
        return preferences.getString(FIREBASE_ID, "")!!
    }

    fun saveTrackedServiceDetails(serviceDetailsUiModel: ServiceDetailsUiModel) {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val serviceDetailsList = Types.newParameterizedType(List::class.java, ServiceDetailsUiModel::class.java)
        val adapter: JsonAdapter<List<ServiceDetailsUiModel>> = moshi.adapter(serviceDetailsList)

        val currentJson = preferences.getString(TRACKED_SERVICE, "")
        val json = if (currentJson.isNullOrEmpty()) {
            adapter.toJson(listOf(serviceDetailsUiModel))
        } else {
            val trackedServices = adapter.fromJson(currentJson)
            adapter.toJson(trackedServices?.plus(serviceDetailsUiModel))
        }
        preferences.edit().putString(TRACKED_SERVICE, json).apply()
    }

    fun retrieveTrackedService(): List<ServiceDetailsUiModel>? {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val serviceDetailsList = Types.newParameterizedType(List::class.java, ServiceDetailsUiModel::class.java)
        val adapter: JsonAdapter<List<ServiceDetailsUiModel>> = moshi.adapter(serviceDetailsList)
        val json = preferences.getString(TRACKED_SERVICE, "")
        return if (!json.isNullOrEmpty()) adapter.fromJson(json) else null
    }

}