package com.cross.line

import android.os.Build
import com.anythink.core.api.ATAdInfo
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

/**
 * Date：2024/11/27
 * Describe:
 */
abstract class BaseNetworkImpl {

    protected val mIoScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val mURL = if (LineUtils.IS_TEST) "" else ""

    protected val mOkHttp = OkHttpClient()

    fun postReferrer(ref: String, success: () -> Unit) {
        val js = getCommonInfo(Build.MODEL, Build.MANUFACTURER).apply {

        }
        postRequest(strToRequest(js.toString()), {}, 20)
    }

    private fun getCommonInfo(module: String = "", manu: String = ""): JSONObject {

        return JSONObject()
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

            }
            jsA.put(js)
        }
        postRequest(strToRequest(jsA.toString()),  retry = 2)
    }

    fun postEvent(string: String, pair: Pair<String, String>? = null) {
        val isNameMust = KnotCenter.listName.contains(string)

        val js = getCommonInfo().apply {

        }
        postRequest(strToRequest(js.toString()), {}, if (isNameMust) 20 else 2)
    }

    fun adValuePost(atAdInfo: ATAdInfo) {
        val js = getCommonInfo().apply {
            put("", atAdInfo.publisherRevenue * 1000000)
            put("", atAdInfo.currency)
            put("", atAdInfo.networkName)
            put("", "topon")
            put("", atAdInfo.placementId)
            put("", "i_line")
            put("", atAdInfo.format)
        }

        postRequest(strToRequest(js.toString()), {}, 2)
    }

    private fun strToRequest(body: String): Request {
        return Request.Builder().post(
            body.toRequestBody("application/json".toMediaType())
        ).url(mURL).build()
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
            Class.forName("com/cross/line/KnotCenter")
                .getMethod("lineNameSetting", String::class.java).invoke(null, "m1hc90")
        }
    }

    protected fun actionPop() {
        runCatching {
            Class.forName("com/cross/line/KnotCenter")
                .getMethod("lineNameSetting", String::class.java).invoke(null, "qz1mhszs12")
        }
    }

}