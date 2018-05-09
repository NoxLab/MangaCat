package dev.tiar.mangacat.utils

import android.content.Context
import android.util.Log
import java.io.File


/**
 * Created by Tiar on 03.2018.
 */

class MemoryCache {
    companion object {
        const val TAG = "MemoryCache"
    }

    fun getCacheSizeSt(context: Context):Int {
        return try {
            val dir = context.cacheDir
            Log.d(TAG, "Cache clear st, size [${getFolderSize(dir)}]")
            getFolderSize(dir).toInt()
        } catch (e: Exception) {
            0
        }
    }

    fun clearStCache(context: Context) {
        try {
            val dir = context.cacheDir
            Log.d(TAG, "Cache clear st, size [${getFolderSize(dir)}]")
            deleteDir(dir)
        } catch (e: Exception) {
        }
    }

    private fun getFolderSize(f: File): Long {
        var size: Long = 0
        if (f.isDirectory) {
            for (file in f.listFiles()!!) {
                size += getFolderSize(file)
            }
        } else {
            size = f.length()
        }
        return size
    }

    private fun deleteDir(dirCache: File?): Boolean {
        if (dirCache != null && dirCache.isDirectory) {
            val children = dirCache.list()
            for (i in children.indices) {
                val success = deleteDir(File(dirCache, children[i]))
                if (!success) {
                    return false
                }
            }
            return dirCache.delete()
        } else return if (dirCache != null && dirCache.isFile) {
            dirCache.delete()
        } else {
            false
        }
    }
}
