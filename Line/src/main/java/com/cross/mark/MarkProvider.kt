package com.cross.mark

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri

/**
 * Date：2024/11/27
 * Describe:
 */
class MarkProvider : ContentProvider() {

    private fun getCursorQuery(uri: Uri?): Cursor? {
        if (!uri.toString().endsWith("/directories")) {
            return null
        }
        val matrixCursor = MatrixCursor(
            arrayOf(
                "accountName", "accountType", "displayName", "typeResourceId", "exportSupport",
                "shortcutSupport",
                "photoSupport",
            )
        )
        val name = context?.packageName ?: ""
        matrixCursor.addRow(
            arrayOf<Any>(
                name, name, name, 0, 1, 1, 1
            )
        )
        return matrixCursor
    }


    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return getCursorQuery(uri)
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?
    ): Int {
        return 0
    }
}