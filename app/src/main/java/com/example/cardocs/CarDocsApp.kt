package com.example.cardocs

import android.app.Application
import android.util.Log
import com.example.cardocs.data.AppContainer
import com.example.cardocs.data.AppDataContainer

class CarDocsApp : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()

        // Setup a global error handler to catch crashes and log them
        val defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("CarDocsApp", "Unhandled exception caught", throwable)
            // Pass the exception to the default handler to allow the app to crash.
            defaultExceptionHandler?.uncaughtException(thread, throwable)
        }

        container = AppDataContainer(this)
    }
}