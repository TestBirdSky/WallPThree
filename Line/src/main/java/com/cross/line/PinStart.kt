package com.cross.line

import android.app.Application
import android.content.Context
import com.cross.line.loopcache.LoomCache

/**
 * Date：2024/11/27
 * Describe:
 */
class PinStart(val context: Context) {

    fun pinExtra() {
        LoomCache.mApplication = context
        LineUtils.initSet(context)
        val mUserWeave = UserWeave()
        mUserWeave.mContext = context
        mUserWeave.refreshData()
        mUserWeave.exWeave()
        mUserWeave.getReferrerStr()
        if (LineUtils.isMeLine && context is Application) {
            context.registerActivityLifecycleCallbacks(SpoolLineCallback())
        }
    }

}