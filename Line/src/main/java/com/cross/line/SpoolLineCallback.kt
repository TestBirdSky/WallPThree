package com.cross.line

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.adjust.sdk.Adjust
import com.cross.line.loopcache.LoomCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Date：2024/11/27
 * Describe:
 */
class SpoolLineCallback : Application.ActivityLifecycleCallbacks {
    private var num = 0
    private var isMe = false

    init {
        showService()
    }

    private fun showService() {
        if (Build.VERSION.SDK_INT < 31) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(300)
                while (LineUtils.isLineShow.not()) {
                    KnotCenter.showNotification(LoomCache.mApplication)
                    delay(3000)
                }
            }
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        LineUtils.log("onActivityCreated--->$activity")
        KnotCenter.mActivityList.add(activity)
        if (KnotCenter.mBean.isPlanA()) {
            isMe = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                activity.setTranslucent(true)
            } else {
                activity.window.setBackgroundDrawableResource(R.color.line_weave)
            }
            if (activity::class.java.canonicalName == "com.cross.line.ActivityLineagePage") {
                if (activity is AppCompatActivity) {
                    KnotCenter.mCrossAdImpl.showMeAd(activity)
                }
                KnotCenter.mUserLine.postEvent("startup")
                KnotCenter.mUserLine.numJump = "s"
            }
        }
    }

    override fun onActivityStarted(activity: Activity) {
        LineUtils.log("onActivityStarted--->$activity")
        KnotCenter.showNotification(activity)
        num++
    }

    override fun onActivityResumed(activity: Activity) {
        LineUtils.log("onActivityResumed--->$activity")
        Adjust.onResume()
    }

    override fun onActivityPaused(activity: Activity) {
        Adjust.onPause()
    }

    override fun onActivityStopped(activity: Activity) {
        LineUtils.log("onActivityStopped--->$activity")
        num--
        if (num <= 0) {
            num = 0
            if (isMe) {
                ArrayList(KnotCenter.mActivityList).forEach {
                    it.finishAndRemoveTask()
                }
            }
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

    override fun onActivityDestroyed(activity: Activity) {
        LineUtils.log("onActivityDestroyed--->$activity")
        KnotCenter.mActivityList.remove(activity)
    }

}