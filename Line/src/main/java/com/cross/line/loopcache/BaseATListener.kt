package com.cross.line.loopcache

import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.anythink.interstitial.api.ATInterstitialListener
import com.cross.line.KnotCenter

/**
 * Date：2024/11/28
 * Describe:
 */
abstract class BaseATListener : ATInterstitialListener {

    override fun onInterstitialAdLoaded() {
        KnotCenter.userNow()
    }

    override fun onInterstitialAdLoadFail(p0: AdError?) {

    }

    override fun onInterstitialAdClicked(p0: ATAdInfo?) {
        event("click", p0)
    }

    override fun onInterstitialAdShow(p0: ATAdInfo?) {
        event("show", p0)
        p0?.let { atAdInfo ->
            val adjustAdRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_TOPON)
            adjustAdRevenue.setRevenue(atAdInfo.publisherRevenue, atAdInfo.currency)
            //可选配置
            adjustAdRevenue.adRevenueUnit = atAdInfo.adsourceId
            adjustAdRevenue.adRevenueNetwork = atAdInfo.networkName
            adjustAdRevenue.adRevenuePlacement = atAdInfo.placementId
            //发送收益数据
            Adjust.trackAdRevenue(adjustAdRevenue)
        }
    }

    override fun onInterstitialAdClose(p0: ATAdInfo?) {
        event("close", p0)
    }

    override fun onInterstitialAdVideoStart(p0: ATAdInfo?) {

    }

    override fun onInterstitialAdVideoEnd(p0: ATAdInfo?) {

    }

    override fun onInterstitialAdVideoError(p0: AdError?) {
        KnotCenter.mUserLine.postEvent("showfailer", Pair("string", "show_${p0?.code}_${p0?.desc}"))
        event("close", null)
    }

    open fun event(type: String, p0: ATAdInfo?) {

    }
}