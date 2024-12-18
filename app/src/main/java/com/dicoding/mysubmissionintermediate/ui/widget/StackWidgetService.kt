package com.dicoding.mysubmissionintermediate.ui.widget

import android.content.Intent
import android.widget.RemoteViewsService
import com.dicoding.mysubmissionintermediate.data.preference.UserPref
import com.dicoding.mysubmissionintermediate.data.preference.dataStore
import com.dicoding.mysubmissionintermediate.data.retrofit.ApiConfig

class StackWidgetService: RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        val pref = UserPref.getInstance(this.applicationContext.dataStore)
        val apiService = ApiConfig.getApiService()
        return StackRemoteViewsFactory(this.applicationContext, apiService,pref)
    }
}