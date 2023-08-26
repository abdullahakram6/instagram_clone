package com.example.firebasekotlin.base

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this);
    }

    companion object {

        @JvmStatic
        var instance: MyApp? = null
            private set

        @JvmStatic
        val context: Context
            get() = instance!!.applicationContext
    }
}