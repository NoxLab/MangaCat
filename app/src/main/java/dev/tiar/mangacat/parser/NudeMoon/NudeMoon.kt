package dev.tiar.mangacat.parser.nHentai

import android.os.AsyncTask
import android.util.Log
import dev.tiar.mangacat.fragments.MainFragment
import dev.tiar.mangacat.items.ItemMain
import dev.tiar.mangacat.items.Statics
import dev.tiar.mangacat.utils.Utils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * Created by Tiar on 03.2018.
 */
class NudeMoon(private val url: String,
              private val callback: MainFragment.OnItemCatListener<ItemMain>,
              private var items: ArrayList<ItemMain>?,
              private var itemPath: ItemMain?) : AsyncTask<Void, Void, Void>() {
    companion object {
        private const val TAG = "NudeMoon"
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
            val post = data.select("table[class='news_pic2']")
            for (cur in post) {
                if (cur.html().contains("img class=\"news_pic2\"")) {
                    var p = " ";
                    var url = url;
                    var title = "";
                    var img = "";
                    var artist = "";
                    var series = ""
                    if (cur.text().contains("Страниц:"))
                        p = cur.text().split("Страниц:")[1].trim()
                    if (p.contains("Отзывы:"))
                        p = p.split("Отзывы:")[0].trim()
                    if (p.contains(" "))
                        p = p.split(" ")[0].trim()

                    var tags = ""
                    if (cur.html().contains("tags"))
                        tags = cur.select("#tags").text().trim()

                    img = "http://nude-moon.com" + cur.select("img").first().attr("src")

                    var pages = ""
                    if (p != " " && p.isNotEmpty())
                        for (i in 0 until p.toInt()) {
                            val pg = if (i < 10) "0$i" else i.toString()
                            pages += img.split("/01.jpg")[0] + pg + ".jpg,"
                        }

                    if (cur.html().contains("bg_style1"))
                        title = cur.select(".bg_style1").first().text()

                    if (cur.html().contains("bg_style1"))
                        url = "http://nude-moon.com" + cur.select(".bg_style1 a").first().attr("href")

                    if (cur.text().contains("Автор:"))
                        artist = cur.text().split("Автор:")[1]
                    if (artist.contains("Перевод:"))
                        artist = artist.split("Перевод:")[0].trim()
                    if (artist.contains("Серия:"))
                        artist = artist.split("Серия:")[0].trim()

                    if (cur.text().contains("Серия:"))
                        series = cur.text().split("Серия:")[1]
                    if (series.contains("Перевод:"))
                        series = series.split("Перевод:")[0].trim()

                    itemPath!!.id.add(" ")
                    itemPath!!.count.add(p.trim())
                    itemPath!!.url.add(url)
                    itemPath!!.title.add(title)
                    itemPath!!.img.add(img)
                    itemPath!!.img_list.add(Utils().deleteLastChar(pages))
                    itemPath!!.tags.add(tags)
                    itemPath!!.tagID.add(tags)
                    itemPath!!.lang.add("russian")
                    itemPath!!.artists.add(artist.trim())
                    itemPath!!.artistsID.add("")
                    itemPath!!.parodies.add(series.trim())
                    itemPath!!.parodiesID.add("")
                    itemPath!!.groups.add("")
                    itemPath!!.groupsID.add("")
                    itemPath!!.characters.add("")
                    itemPath!!.charactersID.add("")

                    items!!.add(itemPath!!)

                    Log.d(TAG, "add $title")
                }
            }
        }
    }

    private fun getData(url: String): Document? {
        return try {
            Log.d(TAG, "getData: get connected to $url")
            Jsoup.connect(url).timeout(10000).ignoreContentType(true).get()
        } catch (e: Exception) {
            Log.d(TAG, "getData: connected false to $url")
            e.printStackTrace()
            null
        }

    }

}