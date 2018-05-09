package dev.tiar.mangacat.adapters

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import dev.tiar.mangacat.R
import dev.tiar.mangacat.items.ItemMain
import dev.tiar.mangacat.items.Statics
import dev.tiar.mangacat.ui.DetailActivity
import dev.tiar.mangacat.ui.ImgActivity
import dev.tiar.mangacat.utils.Utils
import kotlinx.android.synthetic.main.item_catalog.view.*
import java.util.*











/**
 * Created by Tiar on 03.2018.
 */
abstract class MainAdapter(private val context: Context?, private val category: String) : RecyclerView.Adapter<MainAdapter.MainViewHolder>() {
    private var items: ArrayList<ItemMain>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view: View = if (Utils().getGridSize(context) == 2 || category == "more")
            LayoutInflater.from(parent.context).inflate(R.layout.item_catalog, parent, false)
        else
            LayoutInflater.from(parent.context).inflate(R.layout.item_catalog_line, parent, false)
        return MainViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MainViewHolder, pos: Int) {
        val defSettings = HashSet(Arrays.asList("img", "title", "lang"))
        val uiSettings = PreferenceManager.getDefaultSharedPreferences(context).getStringSet("catalog_ui", defSettings)
        val position = holder.adapterPosition
        val cur = items!![position]
        holder.title.text = cur.title[position]
        holder.lang.text = cur.lang[position]
        if (cur.count[position] == "X")
            holder.count.text = cur.count[position]
        else holder.count.text = cur.count[position] + context!!.getString(R.string.PGS)
        holder.tags.text = cur.tags[position].replace(",", " ")
        Glide.with(context!!).load(cur.img[position]).into(holder.img)

        //set visibility
        holder.img.visibility = if (uiSettings.contains("img") || category == "more")
            View.VISIBLE else View.GONE
        holder.title.visibility = if (uiSettings.contains("title") || category == "more")
            View.VISIBLE else View.GONE
        holder.tags.visibility = if (!uiSettings.contains("tags") || category == "more")
            View.GONE else View.VISIBLE
        holder.lang.visibility = if (uiSettings.contains("lang")) View.VISIBLE else View.GONE
        holder.count.visibility = if (uiSettings.contains("count")) View.VISIBLE else View.GONE
        if (!uiSettings.contains("img")) holder.l_lang.setPadding(0,0,0,0)
        if (!uiSettings.contains("title")) {
//            val lp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
//            lp.setMargins(0, 0, 0, 0)
//            holder.img.layoutParams = lp
            val marginParams = MarginLayoutParams(holder.img.layoutParams)
            marginParams.setMargins(0, 0, 0, 0)
            val layoutParams = LinearLayout.LayoutParams(marginParams)
            holder.img.layoutParams = layoutParams
        }

        holder.cardview.isFocusable = true
        holder.cardview.setOnFocusChangeListener({ view, b ->
            Log.d(TAG, "onFocusChange: $position")
            if (!view.isSelected) view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
            else view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGone))
            view.isSelected = b
        })
        if (position == 0 && category != "more")
            holder.cardview.requestFocus()

        holder.cardview.setOnClickListener({
            Statics.item_cur = cur
            val intent = Intent(context, DetailActivity::class.java)
            Log.d(TAG, "$position + ${cur.id[position]} " + cur.title[position] + " " + cur.img[position])
            intent.putExtra("Title", cur.title[position])
            intent.putExtra("Url", cur.url[position])
            intent.putExtra("Img", cur.img[position])
            intent.putExtra("Position", position.toString())
            context.startActivity(intent)
        })
        holder.cardview.setOnLongClickListener({
            val intent = Intent(context, ImgActivity::class.java)
            intent.putExtra("Position", 0)
            intent.putExtra("Img", cur.img_list[position])
            intent.putExtra("Id", position)
            context.startActivity(intent)
            true
        })

        if (position >= itemCount - Utils().calculateGrid(context, 180) &&
                Statics.cur_items < itemCount && category == Statics.CATALOG) {
            //для остановки бессконечной загрузки
            Statics.cur_items = itemCount
            load()
        }
    }

    fun setItems(items: ArrayList<ItemMain>) {
        this.items = items
        Log.d(TAG, "item count : ${this.items!!.size}")
    }

    override fun getItemCount(): Int {
        return if (items != null) {
            items!!.size
        } else 0
    }

    abstract fun load()

    class MainViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var img = view.img_post!!
        var title = view.title_post!!
        var lang = view.lang_post!!
        var count = view.count_post!!
        var tags = view.tags_post!!
        var cardview = view.cardview!!
        var l_lang = view.l_lang!!
    }

}