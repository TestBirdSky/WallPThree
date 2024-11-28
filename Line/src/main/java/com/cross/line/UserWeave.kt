package com.cross.line

import android.content.Context
import android.provider.Settings
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.anythink.core.api.ATSDK
import com.cross.line.loopcache.LoomCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Date：2024/11/27
 * Describe:
 */
class UserWeave {
    lateinit var mContext: Context

    fun refreshData() {
        if (LineUtils.isMeLine.not()) return

        mContext.packageManager.getPackageInfo(mContext.packageName, 0).apply {
            LoomCache.mAppVersionStr = versionName
            LoomCache.mAppInstallTime = firstInstallTime
        }

        if (LoomCache.mAndroidIdStr.isBlank()) {
            LoomCache.mAndroidIdStr =
                Settings.System.getString(mContext.contentResolver, Settings.Secure.ANDROID_ID)
                    .ifBlank { UUID.randomUUID().toString() }
        }
        // todo del and modify
        ATSDK.setNetworkLogDebug(LineUtils.IS_TEST)
        ATSDK.init(mContext, "h670e13c4e3ab6", "ac360a993a659579a11f6df50b9e78639")
    }

    fun exWeave() {
        if (LineUtils.isMeLine.not()) return

        // todo modify
        val environment = AdjustConfig.ENVIRONMENT_SANDBOX
//        if (BuildConfig.DEBUG) AdjustConfig.ENVIRONMENT_SANDBOX else AdjustConfig.ENVIRONMENT_PRODUCTION
        // todo modify adjust key
        val config = AdjustConfig(mContext, "ih2pm2dr3k74", environment)
        config.isSendInBackground = true
        config.isFinalAttributionEnabled = true
        Adjust.addSessionCallbackParameter("customer_user_id", LoomCache.mAndroidIdStr)
        config.setOnAttributionChangedListener {
            LineUtils.log("setOnAttributionChangedListener--->${it.network}")
        }

        Adjust.onCreate(config)

        CoroutineScope(Dispatchers.Main).launch {
            delay(300)
            Adjust.onResume()
        }
    }

    fun getReferrerStr() {
        val mReferrerPulley = ReferrerPulley(mContext)
        mReferrerPulley.getReferrer()
        mReferrerPulley.referrerGetInvoke = { ref, isFirst ->
            val helperConfigure = HelperConfigure()
            helperConfigure.refreshLastConfigure()
            helperConfigure.fetchConfigure(ref, isFirst)
        }
    }

}