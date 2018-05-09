package dev.tiar.mangacat.parser.nHentai

import android.os.AsyncTask
import android.util.Log
import dev.tiar.mangacat.fragments.MainFragment
import dev.tiar.mangacat.items.ItemMain
import dev.tiar.mangacat.items.Statics
import dev.tiar.mangacat.utils.Utils
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * Created by Tiar on 03.2018.
 */
class NHentai(private val url: String,
              private val callback: MainFragment.OnItemCatListener<ItemMain>,
              private var items: ArrayList<ItemMain>?,
              private var itemPath: ItemMain?) : AsyncTask<Void, Void, Void>() {
    companion object {
        private const val TAG = "NHentai"
    }

    override fun onPreExecute() {
        super.onPreExecute()
        Statics.isLoading = true
        if (items == null) items = ArrayList()
        if (itemPath == null) itemPath = ItemMain()
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        Statics.isLoading = false
        callback.onSuccess(items!!, itemPath!!)
    }

    override fun doInBackground(vararg params: Void?): Void? {
        parse(getData(url))
        return null
    }

    private fun parse(data: Document?) {
        if (data != null) {
            Log.d("wtf", Utils().nhentaiJson(data.text()))
            val jsonObj = JSONObject(Utils().nhentaiJson(data.text()))
            val resultJson = jsonObj.getJSONArray("result")
            Log.d(TAG, "posts count : ${resultJson!!.length()}")

            for (i in 0 until resultJson.length()) {
                itemPath!!.id.add(resultJson.getJSONObject(i).getString("media_id"))
                itemPath!!.count.add(resultJson.getJSONObject(i).getString("num_pages"))
                itemPath!!.url.add("https://nhentai.net/api/gallery/${resultJson.getJSONObject(i).getString("id").trim()}")

                var title = resultJson.getJSONObject(i).getJSONObject("title").getString("english").trim()
                title = title.replace("'", "")

                itemPath!!.title.add(title)


                val tagJson = resultJson.getJSONObject(i).getJSONArray("tags")
                var tagName = ""; var tagUrl = ""
                var lang = "undefined"
                var chara = ""; var charaUrl = ""
                var parody = ""; var parodyUrl = ""
                var group = ""; var groupUrl = ""
                var artist = ""; var artistUrl = ""
                for (i in 0 until tagJson!!.length()) {
                    val type = tagJson.getJSONObject(i).getString("type")
                    if (type == "language" && tagJson.getJSONObject(i).getString("name") != "translated")
                        lang = tagJson.getJSONObject(i).getString("name")
                    else if (type == "character") {
                        chara += tagJson.getJSONObject(i).getString("name") + ","
                        charaUrl += tagJson.getJSONObject(i).getString("id") + ","
                    }
                    else if (type == "parody") {
                        parody += tagJson.getJSONObject(i).getString("name") + ","
                        parodyUrl += tagJson.getJSONObject(i).getString("id") + ","
                    }
                    else if (type == "tag") {
                        tagName += tagJson.getJSONObject(i).getString("name") + ","
                        tagUrl += tagJson.getJSONObject(i).getString("id") + ","
                    } else if (type == "group") {
                        group += tagJson.getJSONObject(i).getString("name") + ","
                        groupUrl += tagJson.getJSONObject(i).getString("id") + ","
                    }
                    else if (type == "artist") {
                        artist += tagJson.getJSONObject(i).getString("name") + ","
                        artistUrl += tagJson.getJSONObject(i).getString("id") + ","
                    }
                }


                val imgJson = resultJson.getJSONObject(i).getJSONObject("images")
                val imgId = resultJson.getJSONObject(i).getString("media_id")
                val typeImg = imgJson.getJSONObject("thumbnail").getString("t")
                itemPath!!.img.add("https://t.nhentai.net/galleries/$imgId/thumb" +
                        if (typeImg == "j") ".jpg" else if (typeImg == "p") ".png" else ".gif")

                val pagesJson = imgJson.getJSONArray("pages")
                var pages = ""
                for (i in 0 until pagesJson!!.length()) {
                    val type = pagesJson.getJSONObject(i).getString("t")
                    if (type == "j") pages += "https://t.nhentai.net/galleries/$imgId/${i+ 1}t.jpg,"
                    if (type == "p") pages += "https://t.nhentai.net/galleries/$imgId/${i+ 1}t.png,"
                    if (type == "g") pages += "https://t.nhentai.net/galleries/$imgId/${i+ 1}t.gif,"
                }
//                var type_c = imgJson.getJSONObject("cover").getString("t")
//                itemPath!!.img.add("https://t.nhentai.net/galleries/$imgId/cover" + if (type_c == "j") ".jpg" else ".png")

                itemPath!!.img_list.add(Utils().deleteLastChar(pages))
                itemPath!!.tags.add(Utils().deleteLastChar(tagName))
                itemPath!!.tagID.add(Utils().deleteLastChar(tagUrl))
                itemPath!!.lang.add(lang.replace(",", ""))
                itemPath!!.artists.add(Utils().deleteLastChar(artist))
                itemPath!!.artistsID.add(Utils().deleteLastChar(artistUrl))
                itemPath!!.parodies.add(Utils().deleteLastChar(parody))
                itemPath!!.parodiesID.add(Utils().deleteLastChar(parodyUrl))
                itemPath!!.groups.add(Utils().deleteLastChar(group))
                itemPath!!.groupsID.add(Utils().deleteLastChar(groupUrl))
                itemPath!!.characters.add(Utils().deleteLastChar(chara))
                itemPath!!.charactersID.add(Utils().deleteLastChar(charaUrl))

                items!!.add(itemPath!!)

                Log.d(TAG, "add " + resultJson.getJSONObject(i).getJSONObject("title").getString("english").trim())
            }
        }
    }

    private fun getData(url: String): Document? {
        return try {
            Log.d(TAG, "getData: get connected to $url")
            Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(5000).ignoreContentType(true).get()
        } catch (e: Exception) {
            Log.d(TAG, "getData: connected false to $url")
            e.printStackTrace()
            null
        }

    }

}