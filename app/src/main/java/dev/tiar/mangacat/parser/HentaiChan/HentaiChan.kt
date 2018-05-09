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
class HentaiChan(private val url: String,
              private val callback: MainFragment.OnItemCatListener<ItemMain>,
              private var items: ArrayList<ItemMain>?,
              private var itemPath: ItemMain?) : AsyncTask<Void, Void, Void>() {
    companion object {
        private const val TAG = "HentaiChan"
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
            val post = data.select(".content_row")
            for (cur in post) {
                var p = " "; var url = url; var title = ""; var img = ""; var artist = ""; var series = ""
                if (cur.html().contains("row4_left"))
                    p = cur.select(".row4_left").text()
                if (p.contains("просмотров,") && p.contains(" страниц"))
                    p = p.split("просмотров,")[1].trim().split(" страниц")[0].trim()
                if (p.contains("плюс"))
                    p = "X"
                p = p.replace(" ", "")

                var tags = ""
                if (cur.html().contains("genre"))
                    tags = cur.select(".genre").text().trim()

                if (cur.html().contains("manga_images"))
                    img = cur.select(".manga_images img").first().attr("src")
                img = img.replace("manganew_thumbs", "showfull_retina/manga")

                var pages = ""
                try {
                    for (i in 0 until p.toInt()) {
                        val pg = if (i < 10) "0$i" else i.toString()
                        pages += img.split("/01.jpg")[0] + pg + ".jpg,"
                    }
                } catch (e:Exception) {}


                title = if (cur.html().contains("title_link"))
                    cur.select(".title_link").text()
                else cur.select("a[href^='http://hentai-chan.me/']").text()

                title = title.replace("Глава", "#").replace("глава", "#")
                        .replace("часть", "#").replace("- # ", "#")

                if (cur.html().contains("title_link"))
                    url = "http://hentai-chan.me" + cur.select(".title_link").attr("href")

                if (cur.html().contains("manga_row3"))
                    artist = cur.select(".manga_row3 > .row3_left").first().text().trim()

                if (cur.html().contains("manga_row2"))
                    series = cur.select(".manga_row2 > .row3_left").text().trim()

//                if (!p.contains("плюс")) {
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
//                }
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