package com.cross.line

import android.content.Context
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.cross.line.loopcache.IntLCache
import com.cross.line.loopcache.LoomCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Date：2024/11/27
 * Describe:
 */
class ReferrerPulley(val context: Context) : BaseNetworkImpl() {
    private var isReferrerPost by IntLCache(-1)

    var referrerGetInvoke: (ref: String, isFirst: Boolean) -> Unit = { _, _ -> }

    fun getReferrer() {
        if (LineUtils.isMeLine.not()) return
        if (LoomCache.mReferrer.isBlank()) {
            CoroutineScope(Dispatchers.IO).launch {
                while (LoomCache.mReferrer.isBlank()) {
                    fetchReferrer()
                    delay(16000)
                }
            }
        } else {
            if (isReferrerPost != 100) {
                postReferrer(LoomCache.mReferrer)
            }
            referrerGetInvoke.invoke(LoomCache.mReferrer, false)
        }
    }

    private fun fetchReferrer() {
        if (LoomCache.mReferrer.isNotBlank()) return
        val referrerClient = InstallReferrerClient.newBuilder(context).build()
        referrerClient.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(p0: Int) {
                runCatching {
                    if (p0 == InstallReferrerClient.InstallReferrerResponse.OK) {
                        val response: ReferrerDetails = referrerClient.installReferrer
                        LoomCache.mReferrer = response.installReferrer
                        //todo delete
                        if (LineUtils.IS_TEST) {
                            LineUtils.log("mGoogleReferStr-->${LoomCache.mReferrer}")
                            LoomCache.mReferrer += "adjust"
                        }
                        postReferrer(LoomCache.mReferrer)
                        mIoScope.launch {
                            delay(2111)
                            referrerGetInvoke.invoke(LoomCache.mReferrer, true)
                        }
                        referrerClient.endConnection()
                    } else {
                        referrerClient.endConnection()
                    }
                }.onFailure {
                    referrerClient.endConnection()
                }
            }

            override fun onInstallReferrerServiceDisconnected() = Unit
        })
    }

    private fun postReferrer(mString: String) {
        postReferrer(mString, success = {
            isReferrerPost = 100
        })
    }
}