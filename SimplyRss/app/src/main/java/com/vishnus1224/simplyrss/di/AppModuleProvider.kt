package com.vishnus1224.simplyrss.di

import android.app.Application
import com.vishnus1224.simplyrss.SimplyRss

val appModuleForRealApp = object : AppModule {}

fun provideAppModule(application: Application): AppModule = when (application) {
    is SimplyRss -> appModuleForRealApp
    else -> TODO("Can build a new module here for instrumentation application")
}