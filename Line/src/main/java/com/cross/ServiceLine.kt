package com.cross

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * Date：2024/11/27
 * Describe:
 */
class ServiceLine : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

}