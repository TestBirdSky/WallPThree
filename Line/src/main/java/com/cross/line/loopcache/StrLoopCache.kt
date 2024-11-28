package com.cross.line.loopcache

import kotlin.reflect.KProperty

/**
 * Date：2024/11/27
 * Describe:
 */
class StrLoopCache(val def: String = "") {

    private var cache = ""

    private fun enCodeDataKey(name: String): String {
        return LoomCache.strToBase64("${name}_loop")
    }

    operator fun getValue(me: Any?, p: KProperty<*>): String {
        if (cache.isBlank()) {
            cache = LoomCache.mSp.getString(enCodeDataKey(p.name), def) ?: def
        }
        return cache
    }

    operator fun setValue(me: Any?, p: KProperty<*>, value: String) {
        cache = value
        LoomCache.mSp.edit().putString(enCodeDataKey(p.name), value).apply()
    }

}