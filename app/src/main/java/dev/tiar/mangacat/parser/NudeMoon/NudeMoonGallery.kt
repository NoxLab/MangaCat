package dev.tiar.mangacat.parser.HentaiChan

import android.os.AsyncTask
import android.util.Log
import dev.tiar.mangacat.items.ItemMain
import dev.tiar.mangacat.utils.OnTaskListener
import dev.tiar.mangacat.utils.Utils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * Created by Tiar on 03.2018.
 */
class NudeMoonGallery(private val url: String, private val callback: OnTaskListener<ItemMain>) : AsyncTask<Void, Void, Void>() {
    private var itemPath = ItemMain()
    companion object {
        private const val TAG = "NudeMoonGallery"
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        callback.onSuccess(itemPath)
    }

    override fun doInBackground(vararg params: Void?): Void? {
        parse(getData(url))
        return null
    }

    private fun parse(data: Document?) {
        if (data != null) {
            var p = " "
            val url = url
            var title = ""
            var img = ""
            var artist = ""
            var series = ""

            if (data.html().contains("news_text"))
                title = data.select(".news_text").first().text().trim()

            if (data.html().contains("main-body"))
                p = data.select(".main-body").text().trim()
            if (p.contains("Страниц:"))
                p = p.split("Страниц:")[1].trim()
            if (p.contains("Размер:"))
                p = p.split("Размер:")[0].trim()
            if (p.contains(" "))
                p = p.split(" ")[0].trim()

            img = "http://nude-moon.com" + data.select("img").first().attr("src").trim()
            img = img.replace("22.jpg", "")

            var tags = ""
            if (data.html().contains("tbl2")) {
                val alltags = data.select(".tbl2 a")
                for (tg in alltags) {
                    if (!tg.attr("href").contains("/user/"))
                        tags += tg.attr("href").replace("/tag/", "") + ","
                }
            }

            var pages = ""
            if (url.contains("-online--")) {
                val allpages = data.select("img[src^='/manga/']")
                for (page in allpages) {
                    pages += "http://nude-moon.com" + page.attr("src") + ","
                }
            } else
                pages = img
            if (pages.endsWith(","))
                pages = Utils().deleteLastChar(pages)

            if (data.select(".main-body").text().contains("Автор:"))
                artist = data.select(".main-body").text().split("Автор:")[1].trim()
            if (artist.contains("Перевод:"))
                artist = artist.split("Перевод:")[0].trim()
            if (artist.contains("Серия:"))
                artist = artist.split("Серия:")[0].trim()

            if (data.select(".main-body").text().contains("Серия:"))
                series = data.select(".main-body").text().split("Серия:")[1].trim()
            if (series.contains("Перевод:"))
                series = series.split("Перевод:")[0].trim()

            itemPath.id.add(" ")
            itemPath.count.add(p)
            itemPath.url.add(url)
            itemPath.title.add(title)
            itemPath.img.add(img)

            itemPath.img_list.add(pages.trim())
            itemPath.tags.add(Utils().deleteLastChar(tags).replace("_", " "))
            itemPath.tagID.add(Utils().deleteLastChar(tags))
            itemPath.lang.add("russian")
            itemPath.artists.add(artist)
            itemPath.artistsID.add("")
            itemPath.parodies.add(series)
            itemPath.parodiesID.add("")
            itemPath.groups.add("")
            itemPath.groupsID.add("")
            itemPath.characters.add("")
            itemPath.charactersID.add("")

            Log.d(TAG, "cur $title")
        }
    }

    private fun getData(url: String): Document? {
        return try {
            Log.d(TAG, "getData: get connected to $url")
            Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(10000).ignoreContentType(true).get()
        } catch (e: Exception) {
            Log.d(TAG, "getData: connected false to $url")
            e.printStackTrace()
            null
        }

    }

}