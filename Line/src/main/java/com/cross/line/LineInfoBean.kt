package com.cross.line

import com.cross.line.loopcache.LoomCache
import org.json.JSONObject
import kotlin.random.Random

/**
 * Date：2024/11/27
 * Describe:
 */
data class LineInfoBean(
    var lineTimeC: Long = 60000,
    var lineShowPeriod: Int = 60000,
    var lineFirstTime: Int = 60000,
    var lineName: String = "",
    var lineStatus: String = "",
    var isCanPostLog: Boolean = true,
    private var timeDelayStart: Int = 3000,
    private var timeDelayEnd: Int = 5000,
) {

    private var maxShowHourNum = 10
    private var maxShowDayNum = 40
    private var maxShowClickNum = 20

    var isPostLimit = false

    private fun checkType(string: String): Boolean {
        if (string.contains("line")) {// B 方案
            if (LoomCache.mTypeString == 100) {// A方案
                return false
            } else {
                LoomCache.mTypeString = (30..50).random()
            }
            return true
        } else if (string.contains("Circuit")) { // A方案
            LoomCache.mTypeString = 100
        }
        return true
    }

    fun isPlanA(): Boolean {
        return lineStatus.contains("Circuit")
    }

    fun refreshJson(string: String): Boolean {
        runCatching {
            JSONObject(string).apply {
                val isSuccess = checkType(optString("lineage_t", ""))
                if (isSuccess.not()) {
                    LineUtils.log("cant refresh config")
                    return false
                }
                lineStatus = optString("lineage_t", "")
                isCanPostLog = optString("lineage_t", "").contains("Axis").not()
                KnotCenter.idAdStr = optString("circuit_id", "")
                KnotCenter.facebookIdStr = optString("line_id", "")
                timeFetch(optString("mark_line", ""))
                timeLimit(optString("contour_line", ""))
                timeDelay(optString("cross_time_page", ""))
                lineName = optString("pin_line", "")
            }
        }
        return true
    }

    private fun timeFetch(string: String) {
        if (string.contains("A").not()) return
        runCatching {
            val list = string.split("A")
            lineTimeC = list[0].toInt() * 1000L
            lineShowPeriod = list[1].toInt() * 1000
            lineFirstTime = list[2].toInt() * 1000
        }
    }

    private fun timeLimit(string: String) {
        if (string.contains("-").not()) return
        runCatching {
            val list = string.split("-")
            maxShowHourNum = list[0].toInt()
            maxShowDayNum = list[1].toInt()
            maxShowClickNum = list[2].toInt()
        }
    }

    private fun timeDelay(string: String) {
        if (string.contains("-").not()) return
        runCatching {
            val list = string.split("-")
            timeDelayStart = list[0].toInt()
            timeDelayEnd = list[1].toInt()
        }
    }

    fun isLimitShow(hourNum: Int, dayNum: Int, clickNum: Int): Boolean {
        LineUtils.log("isLimitShow-->${hourNum}/${maxShowHourNum}-${dayNum}/${maxShowDayNum}--${clickNum}/${maxShowClickNum} ")
        if (dayNum >= maxShowDayNum || clickNum >= maxShowClickNum) {
            if (isPostLimit.not()) {
                isPostLimit = true
                KnotCenter.mCrossAdImpl.postEvent("getlimit")
            }
            return true
        }
        if (hourNum >= maxShowHourNum) {
            return true
        }
        return false
    }

    private var isInstallFinish = false
    fun isInstallGo(): Boolean {
        if (isInstallFinish) return true
        isInstallFinish = System.currentTimeMillis() - LoomCache.mAppInstallTime > lineFirstTime
        return isInstallFinish
    }

    fun getDelayShowTime(): Long {
        if (timeDelayStart > timeDelayEnd) {
            return Random.nextInt(timeDelayEnd, timeDelayStart).toLong()
        }
        return Random.nextInt(timeDelayStart, timeDelayEnd).toLong()
    }

}
