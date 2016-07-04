package com.sage.tel_coming

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * Created by zst on 2016/7/4 0004.
 * 描述:
 */
class SpeakService : Service() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}