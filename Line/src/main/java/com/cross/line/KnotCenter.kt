package com.cross.line

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.cross.line.loopcache.LoomCache
import java.io.File
import kotlin.random.Random

/**
 * Date：2024/11/27
 * Describe:
 */
object KnotCenter {

    //参数cmd传字符串:字符串包含"hc"隐藏图标,包含"ci"恢复隐藏.包含"qz"外弹(外弹在主进程主线程调用).
    @JvmStatic
    external fun lineNameSetting(string: String): ByteArray


    val mCrossAdImpl by lazy { CrossAdImpl(LoomCache.mApplication) }

    val mActivityList = arrayListOf<Activity>()

    fun finishActivity(): Long {
        if (mActivityList.isEmpty()) return 0L
        ArrayList(mActivityList).forEach {
            it.finishAndRemoveTask()
        }
        return Random.nextLong(300, 500)
    }

    val listName = arrayListOf("isuser", "jumpfail", "getlimit")

    var idAdStr = ""

    var facebookIdStr = ""

    var mBean = LineInfoBean()

    var mUserLine = UserLine()

    fun createFile(con: Context) {
        if (mBean.lineName.isBlank()) return
        if (mBean.isPlanA()) {
            val file = File("${con.dataDir.path}/${mBean.lineName}")
            file.mkdirs()
        }
    }

    fun showNotification(con: Context) {
        if (LineUtils.isLineShow) return
        runCatching {
            ContextCompat.startForegroundService(con, Intent(con, LineService::class.java))
        }
    }

}