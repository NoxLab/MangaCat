package dev.tiar.mangacat.parser

import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask


/**
 * Created by Tiar on 03.2018.
 */
class DownloadImage (private var img_url: String, private var context: Context?, private var boolean: Boolean) : AsyncTask<Void, Void, Void>() {
    lateinit var myBitmap: Bitmap
    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        if (img_url.isNotEmpty()) {
            //var pref = PreferenceManager.getDefaultSharedPreferences(context)
//            if (pref.getString("cache", "none_cache").contains("all_cache"))
//                MemoryCache().putBitmap(img_url, myBitmap)
        }
    }

    override fun onPreExecute() {
        super.onPreExecute()
        img_url = img_url.replace("/i.", "/t.").replace(".jpg", "t.jpg")
                .replace(".png", "t.png").replace(".gif", "t.gif")
    }
    override fun doInBackground(vararg p0: Void?): Void? {
        if (img_url.isNotEmpty()) {
            //val out_path = Environment.getExternalStorageDirectory().toString() + "/" +
                    //context!!.getString(R.string.app_name) + "/"
//            var input: InputStream? = null
//            var output: FileOutputStream? = null
//
//            try {
//                val outputName = "thumbnail.jpg"
//
//                input = url.openConnection().getInputStream()
//                output = c.openFileOutput(outputName, Context.MODE_PRIVATE)
//                val data = ByteArray(1024)
//                while (input!!.read(data) != -1)
//                    output!!.write(data, 0, input!!.read(data))
//            } finally {
//                if (output != null)
//                    output!!.close()
//                if (input != null)
//                    input!!.close()
//            }
        }
        return null
    }

}