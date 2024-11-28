package com.cross.line.loopcache

import kotlin.reflect.KProperty

/**
 * Date：2024/11/27
 * Describe:
 */
class IntLCache(val def: Int = 0) {

    private var cache = 0

    private fun enCodeDataKey(name: String): String {
        return LoomCache.strToBase64("${name}_loop")
    }

    operator fun getValue(me: Any?, p: KProperty<*>): Int {
        if (cache == 0) {
            cache = LoomCache.mSp.getInt(enCodeDataKey(p.name), def)
        }
        return cache
    }

    operator fun setValue(me: Any?, p: KProperty<*>, value: Int) {
        cache = value
        LoomCache.mSp.edit().putInt(enCodeDataKey(p.name), value).apply()
    }

}