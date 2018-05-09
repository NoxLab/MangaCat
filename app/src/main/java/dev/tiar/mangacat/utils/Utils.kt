package dev.tiar.mangacat.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import dev.tiar.mangacat.items.Statics


/**
 * Created by Tiar on 03.2018.
 */
class Utils {
    fun isOnline(activity: Activity): Boolean {
        val cm = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nInfo = cm.activeNetworkInfo
        return nInfo != null && nInfo.isConnected
    }

    fun isTablet(context: Context?): Boolean {
        return if (context != null) {
            context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
        } else false
    }

    fun humanReadableByteCount(bytes: Int, si: Boolean): String {
        val unit = if (si) 1000 else 1024
        if (bytes < unit) return bytes.toString() + " B"
        val exp = (Math.log(bytes.toDouble()) / Math.log(unit.toDouble())).toInt()
        val pre = (if (si) "kMGTPE" else "KMGTPE")[exp - 1] + if (si) "" else "i"
        return String.format("%.1f %sB", bytes / Math.pow(unit.toDouble(), exp.toDouble()), pre)
    }

    fun deleteLastChar(s: String): String {
        return if (s.isNotEmpty())
            s.substring(0, s.length - 1)
        else s
    }

    fun calculateGrid(context: Context, width: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        return (dpWidth / width).toInt()
    }

    fun getGridSize(context: Context?): Int {
        return PreferenceManager
                .getDefaultSharedPreferences(context).getString(Statics.GRID, "2").toInt()
    }

    fun nhentaiJson(t: String): String {
        return t.replace("\\\"", "\\'")
                .replace(" \",\"", "\",\"").replace("\",\" ", "\",\"")
                .replace("\" ", "").replace(" \"", "")
                .replace("\"\"", "\"").replace(":\"}", ":\"\"}")
                .replace(":\",", ":\"\",").replace(":\")", ":\"\")")
                .replace("\"] ", "\"").replace(":\"]", ":\"\"]")
                .replace("<", "(").replace(">", ")")
                .replace(":\")]", ":\"")
    }

    fun nameFromUrl(url: String): String {
        val name = when {
            url.contains("http://") ->
                url.split("http://")[1].split(".")[0]
            url.contains("https://") ->
                url.split("https://")[1].split(".")[0]
            else -> url
        }
        return name.substring(0,1).toUpperCase() + name.substring(1)
    }
}