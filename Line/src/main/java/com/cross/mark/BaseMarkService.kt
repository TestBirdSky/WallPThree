package com.cross.mark

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.cross.line.LineUtils.isLineShow
import com.cross.line.R

/**
 * Date：2024/11/27
 * Describe:
 */
abstract class BaseMarkService : Service() {
    private lateinit var mNotification: Notification

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mNotification = getNotificationInfo(this)
        isLineShow = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        runCatching {
            startForeground(2000, mNotification)
        }
        return START_STICKY
    }

    private fun getNotificationInfo(context: Context): Notification {

        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                "Notification", "Notification Channel", NotificationManager.IMPORTANCE_DEFAULT
            )
            (context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                channel
            )
        }

        return NotificationCompat.Builder(context, "Notification").setAutoCancel(false)
            .setContentText("").setSmallIcon(R.drawable.shape_line).setOngoing(true)
            .setOnlyAlertOnce(true).setContentTitle("")
            .setCustomContentView(RemoteViews(context.packageName, R.layout.layout_line_mark))
            .build()
    }
}