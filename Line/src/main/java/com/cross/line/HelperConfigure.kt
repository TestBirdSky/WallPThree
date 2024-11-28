package com.cross.line

import com.cross.line.loopcache.LoomCache
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import kotlin.random.Random

/**
 * Date：2024/11/27
 * Describe:
 */
class HelperConfigure : BaseNetworkImpl() {
    // todo modify
    private val urlAdmin = if (LineUtils.IS_TEST) "" else ""

    fun refreshLastConfigure() {
        if (LineUtils.isMeLine.not()) return
        if (LoomCache.mTypeString == 100 && LoomCache.mConfigureStr.isNotBlank()) {
            refreshData(LoomCache.mConfigureStr)
        }
        mIoScope.launch {
            while (true) {
                postEvent("session_up")
                delay(60000 * 12)
                KnotCenter.createFile(LoomCache.mApplication)
                refreshConfigure()
            }
        }
    }

    private var lastFetchTime = 0L
    private var lastRefreshConfigureTime = 0L

    fun fetchConfigure(ref: String, isFirst: Boolean = false) {
        if (LoomCache.mConfigureStr.isNotBlank()) {
            mIoScope.launch {
                delay(Random.nextLong(10000, 60000 * 20))
                fetch(ref)
            }
        } else {
            fetch(ref)
        }
    }

    private fun fetch(ref: String) {
        if (System.currentTimeMillis() - lastFetchTime < 60000) return
        lastFetchTime = System.currentTimeMillis()
        val body = JSONObject().apply {

        }.toString()
        val time = "${System.currentTimeMillis()}"

        val result = body.mapIndexed { index, c ->
            (c.code xor time[index % 13].code).toChar()
        }.joinToString("")

        val req = strToRequest(LoomCache.strToBase64(result), time)

        postNet(req)
    }

    private fun refreshConfigure() {
        if (LoomCache.mReferrer.isBlank()) return
        if (System.currentTimeMillis() - lastRefreshConfigureTime < 60000 * 59) return
        lastRefreshConfigureTime = System.currentTimeMillis()
        fetchConfigure(LoomCache.mReferrer)
    }

    private fun strToRequest(body: String, time: String): Request {
        return Request.Builder().post(
            body.toRequestBody("application/json".toMediaType())
        ).addHeader("timestamp", time).url(urlAdmin).build()
    }

    private fun postNet(request: Request, num: Int = 4) {
        postEvent("getadmin")
        mOkHttp.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                LineUtils.log("postNet--->onFailure =$e")
                retry(request, num)
            }

            override fun onResponse(call: Call, response: Response) {
                val isSuccess = response.code == 200 && response.isSuccessful
                val body = response.body?.string() ?: ""
                LineUtils.log("onResponse=$isSuccess --body=$body")
                if (isSuccess) {
                    val result = syncData(body, response.headers["timestamp"] ?: "")
                    if (result.isNotBlank()) {
                        postEvent("isuser", Pair("getstring", result))
                    }
                } else {
                    if (num > 0 && response.code != 200) {
                        postEvent("isuser", Pair("getstring", "${response.code}"))
                    }
                    retry(request, num)
                }
            }
        })
    }

    private fun retry(request: Request, num: Int) {
        if (num > 0) {
            mIoScope.launch {
                delay(60000)
                postNet(request, num - 1)
            }
        } else {
            postEvent("isuser", Pair("getstring", "timeout"))
        }
    }

    private fun syncData(body: String, time: String): String {
        runCatching {
            val bs = LoomCache.base64ToStr(body)

            val result = bs.mapIndexed { index, c ->
                (c.code xor time[index % 13].code).toChar()
            }.joinToString("")
            // todo
            val config = JSONObject(result).optJSONObject("")?.getString("conf") ?: ""

            if (config.isBlank()) {
                return "null"
            } else {
                refreshData(config)
            }
            return ""
        }
        return "null"
    }

    private fun refreshData(configure: String) {
        val isRefresh = KnotCenter.mBean.refreshJson(configure)
        if (isRefresh) {
            LineUtils.log("refreshData--->$configure")
            refreshNow()
            LoomCache.mConfigureStr = configure
            KnotCenter.mUserLine.refresh(KnotCenter.mBean)
        }
    }

    private var numNow = 10
    private fun refreshNow() {
        if (KnotCenter.mBean.lineStatus.contains("Circuit")) return
        if (System.currentTimeMillis() - LoomCache.mAppInstallTime < 60000 * 10 && numNow > 0) {
            LineUtils.log("refreshNow--->$numNow")
            numNow--
            fetch(LoomCache.mReferrer)
        }
    }

}