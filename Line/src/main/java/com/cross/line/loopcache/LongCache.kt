package com.cross.line.loopcache

import kotlin.reflect.KProperty

/**
 * Date：2024/11/27
 * Describe:
 */
class LongCache(val def: Long = 0) {
    private var cache = 0L

    private fun enCodeDataKey(name: String): String {
        return LoomCache.strToBase64("${name}_Coil")
    }

    operator fun getValue(me: Any?, p: KProperty<*>): Long {
        if (cache == 0L) {
            cache = LoomCache.mSp.getLong(enCodeDataKey(p.name), def)
        }
        return cache
    }

    operator fun setValue(me: Any?, p: KProperty<*>, value: Long) {
        cache = value
        LoomCache.mSp.edit().putLong(enCodeDataKey(p.name), value).apply()
    }
}