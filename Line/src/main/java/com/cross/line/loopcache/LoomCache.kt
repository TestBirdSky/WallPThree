package com.cross.line.loopcache

import android.content.Context
import android.util.Base64

/**
 * Date：2024/11/27
 * Describe:
 */
object LoomCache {
    lateinit var mApplication: Context

    val mSp by lazy { mApplication.getSharedPreferences("Line", 0) }

    var mAndroidIdStr by StrLoopCache("")

    var mReferrer by StrLoopCache()

    var mAppVersionStr = "1.0.1"

    var mAppInstallTime = 0L

    var mConfigureStr by StrLoopCache()

    var mTypeString by IntLCache(-1) // 类型 100 A 类型 50 B类型

    fun strToBase64(string: String): String {
        return Base64.encodeToString(string.toByteArray(), Base64.DEFAULT)
    }

    fun base64ToStr(str: String): String {
        return String(Base64.decode(str, Base64.DEFAULT))
    }

}