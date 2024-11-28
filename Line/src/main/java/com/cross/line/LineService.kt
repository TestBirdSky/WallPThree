package com.cross.line

import android.content.Intent
import com.cross.mark.BaseMarkService

/**
 * Date：2024/11/27
 * Describe:
 */
class LineService : BaseMarkService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }
}