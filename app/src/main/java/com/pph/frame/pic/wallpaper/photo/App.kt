package com.pph.frame.pic.wallpaper.photo

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.tencent.mmkv.MMKV
import android.os.Environment
import com.cross.line.PinStart
import java.io.File
import java.io.OutputStream

class App : Application() {


    companion object {


        fun saveBitmap(mContext: Context, bmp: Bitmap, callYes: () -> Unit, callNo: () -> Unit) {

            try {
                val fileName = "IMG_" + System.currentTimeMillis()
                val values = ContentValues()
                values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                values.put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis())
                values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
                values.put(MediaStore.Images.Media.WIDTH, bmp.width)
                values.put(MediaStore.Images.Media.HEIGHT, bmp.height)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.put(
                        MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES
                    )
                } else {
                    val path: String =
                        Environment.getExternalStorageDirectory().path + File.separator + Environment.DIRECTORY_PICTURES + File.separator + fileName
                    values.put(MediaStore.Images.Media.DATA, path)
                }

                val uri: Uri? = mContext.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
                )
                if (null != uri) {
                    val out: OutputStream? = mContext.contentResolver.openOutputStream(uri)
                    if (null != out) {
                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, out)
                        out.close()
                        callYes.invoke()
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                callNo.invoke()
            }
        }


        lateinit var mmkv: MMKV
        lateinit var app: App

        fun putSet(key: String, value: Set<String>) {
            mmkv.encode(key, value)
        }

        fun getSet(key: String): MutableSet<String> {
            return mmkv.decodeStringSet(key) ?: mutableSetOf()
        }

        val KEY_LIKE = "KEY_LIKE"


        fun addLike(time: Long, resId: Int) {
            val set = getSet(KEY_LIKE)
            set.add("$time###$resId")
            putSet(KEY_LIKE, set)
        }

        fun getLikeList(): MutableList<Info> {
            val set = getSet(KEY_LIKE)
            val list = set.map {
                val list: List<String> = it.split("###")
                Info(list.get(0).toLong(), list.get(1).toInt());
            }.toMutableList()

            list.sortBy { it.time }
            return list;
        }

        fun isLike(resId: Int): Boolean {
            val set = getSet(KEY_LIKE)
            for ((index, str) in set.withIndex()) {
                if (str.contains("$resId")) {
                    return true
                }
            }
            return false
        }

        fun removeLike(resId: Int) {
            val set = getSet(KEY_LIKE)
            for ((index, str) in set.withIndex()) {
                if (str.contains("$resId")) {
                    set.remove(str)
                    putSet(KEY_LIKE, set)
                    return
                }
            }
        }


    }

    override fun onCreate() {
        super.onCreate()
        app = this;
        MMKV.initialize(this)
        mmkv = MMKV.defaultMMKV()
        val pinStart = PinStart(this)
        pinStart.pinExtra()
    }
}