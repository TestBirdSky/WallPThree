package com.cross.mark

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Date：2024/11/27
 * Describe:
 */
abstract class BaseReceiver : BroadcastReceiver() {

    abstract fun action(intent: Intent)

    abstract fun setContext(context: Context)

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        setContext(context)
        if (intent.hasExtra("P")) {
            val eIntent = intent.getParcelableExtra("P") as Intent?
            if (eIntent != null) {
                action(eIntent)
            }
        }
    }

}