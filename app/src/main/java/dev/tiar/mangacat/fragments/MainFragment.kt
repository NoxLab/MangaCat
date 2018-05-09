package dev.tiar.mangacat.fragments

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.res.Configuration
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import dev.tiar.mangacat.R
import dev.tiar.mangacat.adapters.MainAdapter
import dev.tiar.mangacat.items.ItemMain
import dev.tiar.mangacat.items.Statics
import dev.tiar.mangacat.parser.nHentai.HentaiChan
import dev.tiar.mangacat.parser.nHentai.NHentai
import dev.tiar.mangacat.parser.nHentai.NudeMoon
import dev.tiar.mangacat.utils.DBHelper
import dev.tiar.mangacat.utils.Utils
import kotlinx.android.synthetic.main.pb.view.*

@SuppressLint("ValidFragment")
/**
 * Created by Tiar on 03.2018.
 */
data class MainFragment(private val url: String) : Fragment() {
    var dB: String = ""
    private lateinit var rv: RecyclerView
    private lateinit var pb: LinearLayout
    var itemsCatalog = ArrayList<ItemMain>()
    var itemPath = ItemMain()
    private var curPage = 1

    interface OnItemCatListener<T> {
        fun onSuccess(items: ArrayList<ItemMain>, item: ItemMain)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dB = if (url == Statics.FAVORITE || url == Statics.HISTORY || url == Statics.DOWNLOAD) url
        else Statics.CATALOG

        val view: View = inflater.inflate(R.layout.fragment_main, container, false)
        rv = view.findViewById<RecyclerView>(R.id.rv_catalog) as RecyclerView
        pb = view.findViewById<LinearLayout>(R.id.progressL) as LinearLayout
        val gridSize = if (Utils().getGridSize(context!!) == 1) 1 else Utils().calculateGrid(context!!, 180)

        view.img_pb.setBackgroundResource(R.drawable.loading_anim)
        val loadingAnim = view.img_pb.background as AnimationDrawable
        loadingAnim.start()
        rv.layoutManager = GridLayoutManager(context, gridSize)
        val adapter = object : MainAdapter(context, dB) {
            override fun load() {
                if (dB == Statics.CATALOG && !Statics.isLoading) {
                    curPage++
                    onPage()
                    Log.d(ContentValues.TAG, "load: cur_page - $curPage")
                }
            }
        }
        rv.adapter = adapter
        if (dB == Statics.CATALOG) onPage()
        else {
            pb.visibility = View.GONE
            updateRecycler(DBHelper(context).getDbItems(dB))
        }
        return view
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        val gridSize = if (Utils().getGridSize(context!!) == 1) 1 else Utils().calculateGrid(context!!, 180)
        rv.layoutManager = GridLayoutManager(context, gridSize)
    }

    private fun onPage() {
        val url = url.replace(" ", "")
        pb.visibility = View.VISIBLE
        when {
            url.contains("nhentai.net") -> {
                val t = if (url.contains("galleries/search") || url.contains("galleries/tagged")) "&" else "?"
                val someTask = NHentai("$url${t}page=$curPage", object : OnItemCatListener<ItemMain> {
                    override fun onSuccess(items: ArrayList<ItemMain>, item: ItemMain) {
                        itemsCatalog = items
                        itemPath = item
                        updateRecycler(itemsCatalog)
                        pb.visibility = View.GONE
                    }
                }, itemsCatalog, itemPath)
                someTask.execute()
            }
            url.contains("hentai-chan.me") -> {
                var p = "?offset=${(curPage - 1) * 20}"
                if (url.contains("?do=search"))
                    p = "&search_start=$curPage"
                if (url.contains("/tags/"))
                    p = "&sort=manga&rowstart=${(curPage - 1) * 20}"
                val someTask = HentaiChan("$url$p", object : OnItemCatListener<ItemMain> {
                    override fun onSuccess(items: ArrayList<ItemMain>, item: ItemMain) {
//                        if (itemsCatalog[itemsCatalog.size - 1].title.toString()
//                                        .contains(items[items.size - 1].title.toString()) && curPage > 1)
//                            Statics.isLoading = true
//                        else {
                            itemsCatalog = items
                            itemPath = item
                            updateRecycler(itemsCatalog)
//                        }
                        pb.visibility = View.GONE
                    }
                }, itemsCatalog, itemPath)
                someTask.execute()
            }
            url.contains("nude-moon.com") -> {
                var p = "?rowstart=${(curPage - 1) * 30}"
                if (url.contains("/tag/"))
                    p = "&rowstart=${(curPage - 1) * 30}"
                if (url.contains("search?stext"))
                    p = ""
                val someTask = NudeMoon("$url$p", object : OnItemCatListener<ItemMain> {
                    override fun onSuccess(items: ArrayList<ItemMain>, item: ItemMain) {
//                        if (itemsCatalog[itemsCatalog.size - 1].title.toString()
//                                        .contains(items[items.size - 1].title.toString()) && curPage > 1)
//                            Statics.isLoading = true
//                        else {
                            itemsCatalog = items
                            itemPath = item
                            updateRecycler(itemsCatalog)
//                        }
//                        Log.d("qwe 1", itemPath.title.toString())
//                        Log.d("qwe 2", item.title.toString())
                        pb.visibility = View.GONE
                    }
                }, itemsCatalog, itemPath)
                someTask.execute()
            }
        }
    }

    private fun updateRecycler(items: ArrayList<ItemMain>){
        (rv.adapter as MainAdapter).setItems(items)
        rv.recycledViewPool.clear()
        rv.adapter.notifyItemChanged(0)
    }

    constructor() : this(url = "")
}