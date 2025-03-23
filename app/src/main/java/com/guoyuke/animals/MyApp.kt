package com.guoyuke.animals

import android.app.Application
import android.content.Context

class MyApp: Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        INSTANCE = this
    }

    companion object {
        lateinit var INSTANCE: Application
    }
}