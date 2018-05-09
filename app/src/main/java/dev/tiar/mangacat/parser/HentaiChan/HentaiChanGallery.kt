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
class HentaiChanGallery(private val url: String, private val callback: OnTaskListener<ItemMain>) : AsyncTask<Void, Void, Void>() {
    private var itemPath = ItemMain()
    companion object {
        private const val TAG = "NHentaiGallery"
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
            if (data.html().contains("Доступ ограничен. Только зарегистрированные пользователи подтвердившие")) {
                parse(getData(url.replace("hentai-chan.me", "exhentaidono.me") +
                        "?development_access=true"))
            } else {
                var p = " ";
                var url = url;
                var title = "";
                var img = "";
                var artist = "";
                var series = ""

                if (data.html().contains("title_top_a"))
                    title = data.select(".title_top_a").text().trim()
                title = title.replace("Глава", "#").replace("глава", "#")
                        .replace("часть", "#").replace("- # ", "#")
                if (data.html().contains("title_top_a"))
                    url = data.select(".title_top_a").attr("href").trim()
                if (data.html().contains("row4_left"))
                    p = data.select(".row4_left").text().trim()
                if (p.contains("загрузок,") && p.contains(" страниц"))
                    p = p.split("загрузок,")[1].trim().split(" страниц")[0].trim()
                if (data.html().contains("cover"))
                    img = data.select("#cover").attr("src").trim()
                img = img.replace("manganew_thumbs", "showfull_retina/manga")

                var tags = ""
                if (data.html().contains("sidetag")) {
                    val alltags = data.select(".sidetag")
                    for (tg in alltags) {
                        tags += tg.select("a").last().attr("href")
                                .replace("/tags/", "") + ","
                    }
                }

                var pages = ""
                if (data.html().contains("var data =")) {
                    var allpages = data.html().split("var data =")[1]
                    if (allpages.contains("var manga ="))
                        allpages = allpages.split("var manga =")[0]
                    else if (allpages.contains("createGallery(data)"))
                        allpages = allpages.split("createGallery(data)")[0]

                    if (allpages.contains("\"thumbs\":["))
                        pages = allpages.split("\"thumbs\":[")[1].split("]")[0]
                    else if (allpages.contains("\"thumbs\": ["))
                        pages = allpages.split("\"thumbs\": [")[1].split("]")[0]
                } else
                    pages = "$img,"
                pages = pages.replace("\"", "").replace("'", "")
                        .replace(" ", "").trim()

                if (data.html().contains("info_wrap"))
                    artist = data.select("#info_wrap").text()
                if (artist.contains("Автор ")) {
                    artist = artist.split("Автор ")[1].trim()
                    if (artist.contains(" Переводчик"))
                        artist = artist.split(" Переводчик")[0].trim()
                    else if (artist.contains(" Серия"))
                        artist = artist.split(" Серия")[0].trim()
                }
                if (data.html().contains("info_wrap"))
                    series = data.select("#info_wrap").text().trim()
                if (series.contains("Тип Визуальная новелла"))
                    series = "Визуальная новелла"
                else if (series.contains("Серия ")) {
                    series = series.split("Серия ")[1].trim()
                    if (series.contains("Автор"))
                        series = series.split("Автор")[0].trim()
                    else if (series.contains("Тип"))
                        series = series.split("Тип")[0].trim()
                }

                itemPath.id.add(" ")
                itemPath.count.add(p)
                itemPath.url.add(url)
                itemPath.title.add(title)
                itemPath.img.add(img)

                itemPath.img_list.add(Utils().deleteLastChar(pages.trim()))
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