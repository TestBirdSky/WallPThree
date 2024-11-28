package com.cross.line

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustEvent
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.anythink.interstitial.api.ATInterstitial
import com.cross.line.loopcache.BaseATListener
import com.cross.line.loopcache.IntLCache
import com.cross.line.loopcache.LongCache
import com.cross.line.loopcache.LoomCache
import com.facebook.appevents.AppEventsLogger
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Currency

/**
 * Date：2024/11/27
 * Describe:
 */
class CrossAdImpl(val context: Context) : BaseNetworkImpl() {
    private var mInterstitialAd: ATInterstitial? = null
    private var isCrossing = false
    private var lastCrossingTime = 0L
    private var showNumHour by IntLCache()
    private var showNumDay by IntLCache()
    private var clickNumDay by IntLCache()
    private var dayIndex by IntLCache()
    private var hourTime by LongCache()
    private val day = 60000 * 60 * 24

    private fun checkTime() {
        val index = ((System.currentTimeMillis() - LoomCache.mAppInstallTime) / day).toInt()
        if (index != dayIndex) {
            LineUtils.log("not cur day --->$index")
            dayIndex = index
            showNumHour = 0
            showNumDay = 0
            KnotCenter.mBean.isPostLimit = false
            clickNumDay = 0
        }
        if (System.currentTimeMillis() - hourTime > 60000 * 60) {
            hourTime = System.currentTimeMillis()
            LineUtils.log("not cur hour --->")
            showNumHour = 0
        }
    }

    fun isLimit(): Boolean {
        checkTime()
        return KnotCenter.mBean.isLimitShow(showNumHour, showNumDay, clickNumDay)
    }

    fun loadAdLine() {
        if (KnotCenter.idAdStr.isBlank()) return
        if (isCrossing && System.currentTimeMillis() - lastCrossingTime < 60000) return
        if (isLimit()) return
        if (isAdReady()) {
            LineUtils.log("ad is ready -->")
            return
        }
        isCrossing = true
        lastCrossingTime = System.currentTimeMillis()
        postEvent("reqprogress")
        mInterstitialAd = ATInterstitial(context, KnotCenter.idAdStr)
        mInterstitialAd?.setAdListener(object : BaseATListener() {
            override fun onInterstitialAdLoaded() {
                super.onInterstitialAdLoaded()
                isCrossing = false
                postEvent("getprogress")
            }

            override fun onInterstitialAdLoadFail(p0: AdError?) {
                super.onInterstitialAdLoadFail(p0)
                postEvent("showfailer", Pair("string", "${p0?.code}_${p0?.desc}"))
                mIoScope.launch {
                    delay(20000)
                    isCrossing = false
                }
            }
        })
        mInterstitialAd?.load()
    }

    fun isAdReady(): Boolean {
        val ad = mInterstitialAd ?: return false
        return ad.isAdReady
    }

    private var lastShowEventTime = 0L

    private var job: Job? = null
    fun showMeAd(activity: AppCompatActivity) {
        job?.cancel()
        job = activity.lifecycleScope.launch {
            val del = KnotCenter.mBean.getDelayShowTime()
            delay(del)
            postEvent("delaytime", Pair("time", "$del"))
            val ad = mInterstitialAd
            if (ad != null && isAdReady()) {
                ad.setAdListener(object : BaseATListener() {
                    override fun event(type: String, p0: ATAdInfo?) {
                        super.event(type, p0)
                        post(type, p0) {
                            activity.finishAndRemoveTask()
                        }
                    }
                })
                lastShowEventTime = System.currentTimeMillis()
                ad.show(activity)
                mInterstitialAd = null
            } else {
                postEvent("showfailer", Pair("string", "show_failed_ad_not_ready"))
            }
        }
    }

    private fun post(type: String, po: ATAdInfo?, activityFinish: () -> Unit) {
        when (type) {
            "show" -> {
                KnotCenter.mUserLine.lastShowTime = System.currentTimeMillis()
                showEventMe()
                postEvent(
                    "showsuccess", Pair("t", "${System.currentTimeMillis() - lastShowEventTime}")
                )
                po?.let {
                    adValuePost(it)
                    runCatching {
                        //fb purchase
                        AppEventsLogger.newLogger(context).logPurchase(
                            (it.publisherRevenue).toBigDecimal(), Currency.getInstance("USD")
                        )
                    }
                }
            }

            "close" -> activityFinish.invoke()

            "click" -> clickNumDay++
        }
    }

    private fun showEventMe() {
        showNumHour++
        showNumDay++
        // todo
        val tra: AdjustEvent? = when (showNumDay) {
            10 -> AdjustEvent("")
            20 -> AdjustEvent("")
            30 -> AdjustEvent("")
            40 -> AdjustEvent("")
            50 -> AdjustEvent("")
            else -> null
        }
        if (tra != null) {
            Adjust.trackEvent(tra)
        }
    }


}