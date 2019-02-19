package com.vishnus1224.simplyrss

import android.support.multidex.MultiDexApplication
import com.vishnus1224.simplyrss.di.AppComponentImpl
import com.vishnus1224.simplyrss.di.AppModule

class SimplyRss : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        val appComponent = AppComponentImpl(this)
        AppModule.init(appComponent)
    }
}