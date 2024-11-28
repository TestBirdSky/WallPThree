package com.cross.line

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import android.webkit.WebView
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger

/**
 * Date：2024/11/27
 * Describe:
 */
object LineUtils {

    var isLineShow = false
    var isMeLine = false

    @JvmStatic
    fun initSet(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val processName: String = Application.getProcessName()
            if (!context.packageName.equals(processName)) {
                WebView.setDataDirectorySuffix(processName)
            }
        }
        isMeLine = isMeProgress(context)
    }


    @JvmStatic
    private fun isMeProgress(context: Context): Boolean {
        return context.packageName == context.getProName()
    }

    @JvmStatic
    private fun Context.getProName(): String {
        runCatching {
            val am = getSystemService(Application.ACTIVITY_SERVICE) as ActivityManager
            val runningApps = am.runningAppProcesses ?: return ""
            for (info in runningApps) {
                when (info.pid) {
                    android.os.Process.myPid() -> return info.processName
                }
            }
        }
        return ""
    }

    // todo del
    const val IS_TEST = true

    private val TAG = "Line"

    fun log(msg: String) {
        if (IS_TEST) Log.e(TAG, msg)
    }

    private var isInitFace = false

    @JvmStatic
    fun initFacebook(context: Context) {
        if (KnotCenter.facebookIdStr.isBlank()) return
        if (isInitFace) return
        runCatching {
            FacebookSdk.setApplicationId(KnotCenter.facebookIdStr)
            FacebookSdk.sdkInitialize(context)
            AppEventsLogger.activateApp(context as Application)
            isInitFace = true
        }
    }
}