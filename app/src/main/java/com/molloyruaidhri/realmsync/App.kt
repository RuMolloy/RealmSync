package com.molloyruaidhri.realmsync

import android.app.Application
import io.realm.Realm
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration

class App: Application() {

    companion object {
        lateinit var app: App
    }

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)

        app = App(AppConfiguration.Builder(resources.getString(R.string.app_id)).build())
    }
}