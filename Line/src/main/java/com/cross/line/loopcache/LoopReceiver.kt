package com.cross.line.loopcache

import android.content.Context
import android.content.Intent
import com.cross.mark.BaseReceiver

/**
 * Date：2024/11/27
 * Describe:
 */
class LoopReceiver : BaseReceiver() {
    private var mContext: Context? = null

    override fun action(intent: Intent) {
        runCatching {
            mContext?.startActivity(intent)
        }
    }

    override fun setContext(context: Context) {
        mContext = context
    }

}