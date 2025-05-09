package com.example.replicateai.app

import android.app.Application
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class MyApplication : Application()
//    , Configuration.Provider
{
//    override val workManagerConfiguration: Configuration
//        get() = Configuration.Builder()
//            .setMinimumLoggingLevel(android.util.Log.INFO)
//            .build()
}
