package dev.tiar.mangacat.adapters

import android.content.ContentValues.TAG
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import dev.tiar.mangacat.R
import kotlinx.android.synthetic.main.item_manga.view.*

/**
 * Created by Tiar on 03.2018.
 */
abstract class MangaAdapter(private val context: Context) : RecyclerView.Adapter<MangaAdapter.MangaViewHolder>() {
    private var items: java.util.ArrayList<String>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MangaAdapter.MangaViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_manga, parent, false)
        return MangaAdapter.MangaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MangaViewHolder, position: Int) {
        Glide.with(context).load(items!![position]).into(holder.img)
        holder.img.setOnClickListener({
            open(position)
        })
    }

    fun setImgItems(imgItems: ArrayList<String>) {
        this.items = imgItems
        Log.d(TAG, "item count : ${this.items!!.size}")
    }

    override fun getItemCount(): Int {
        return if (items != null) items!!.size else 0
    }

    class MangaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var img = view.img_manga_thumb!!
    }

    abstract fun open(pos: Int)
}