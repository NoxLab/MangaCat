package dev.tiar.mangacat.ui

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import com.bumptech.glide.Glide
import dev.tiar.mangacat.R
import dev.tiar.mangacat.adapters.MainAdapter
import dev.tiar.mangacat.adapters.MangaAdapter
import dev.tiar.mangacat.fragments.MainFragment
import dev.tiar.mangacat.items.ItemMain
import dev.tiar.mangacat.items.Statics
import dev.tiar.mangacat.parser.HentaiChan.HentaiChanGallery
import dev.tiar.mangacat.parser.HentaiChan.NudeMoonGallery
import dev.tiar.mangacat.parser.nHentai.NHentai
import dev.tiar.mangacat.parser.nHentai.NHentaiGallery
import dev.tiar.mangacat.utils.DBHelper
import dev.tiar.mangacat.utils.OnTaskListener
import dev.tiar.mangacat.utils.Utils
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*


class DetailActivity : AppCompatActivity() {
    private lateinit var pref : SharedPreferences
    private lateinit var title: String
    private lateinit var thumbsCount: String
    private var currentItem = Statics.item_cur
    private var posOfItem = 0
    private var currentUrl = ""

    private var noImage = false
    private var thumbLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pref = PreferenceManager.getDefaultSharedPreferences(this)
        thumbsCount = pref.getString("thumbs_count", "999")
        if (pref.getBoolean("fullscreen", false)) {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val loadingAnim = img_progressLD.background as AnimationDrawable
        loadingAnim.start()

        rv_more_d.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val adapterMore = object : MainAdapter(this, "more") {
            override fun load() {}
        }
        rv_more_d.isNestedScrollingEnabled = false
        rv_more_d.adapter = adapterMore

        title = intent.extras.getString("Title")
        currentUrl = intent.extras.getString("Url")

        if (intent.extras.getString("Position").toInt() < Statics.item_cur.title.size) {
            setInfo(Statics.item_cur, intent.extras.getString("Position").toInt())
            onLoad(intent.extras.getString("Url"), "info")
        } else finish()
    }


    private fun onLoad(url: String, more: String) {
        progressLD.visibility = View.VISIBLE
        if (url.contains("nhentai.net")) {
            if (more == "more") {
                val someTask = NHentai("$url/related", object : MainFragment.OnItemCatListener<ItemMain> {
                    override fun onSuccess(items: ArrayList<ItemMain>, item: ItemMain) {
                        (rv_more_d.adapter as MainAdapter).setItems(items)
                        rv_more_d.recycledViewPool.clear()
                        rv_more_d.adapter.notifyItemChanged(0)
                    }
                }, null, null)
                someTask.execute()
            } else {
                val someTask = NHentaiGallery(url, object : OnTaskListener<ItemMain> {
                    override fun onGetAllItem(items: ArrayList<ItemMain>) {}
                    override fun onSuccess(item: ItemMain) {
                        progressLD.visibility = View.GONE
                        addToDB(Statics.HISTORY, item, 0)
                        setInfo(item, 0)
                    }
                })
                someTask.execute()
                onLoad(url, "more")
            }
        } else if (url.contains("hentai-chan.me")) {
            if (more == "img") {
                val someTask = HentaiChanGallery(url, object : OnTaskListener<ItemMain> {
                    override fun onGetAllItem(items: ArrayList<ItemMain>) {}
                    override fun onSuccess(item: ItemMain) {
                        progressLD.visibility = View.GONE
                        setThumbs(item, 0)
                    }
                })
                someTask.execute()
            } else {
                val someTask = HentaiChanGallery(url, object : OnTaskListener<ItemMain> {
                    override fun onGetAllItem(items: ArrayList<ItemMain>) {}
                    override fun onSuccess(item: ItemMain) {
                        addToDB(Statics.HISTORY, item, 0)
                        setInfo(item, 0)
                    }
                })
                someTask.execute()
            }
        } else if (url.contains("nude-moon.com")) {
            if (more == "img") {
                val someTask = NudeMoonGallery(url, object : OnTaskListener<ItemMain> {
                    override fun onGetAllItem(items: ArrayList<ItemMain>) {}
                    override fun onSuccess(item: ItemMain) {
                        progressLD.visibility = View.GONE
                        setThumbs(item, 0)
                    }
                })
                someTask.execute()
            } else {
                val someTask = NudeMoonGallery(url, object : OnTaskListener<ItemMain> {
                    override fun onGetAllItem(items: ArrayList<ItemMain>) {}
                    override fun onSuccess(item: ItemMain) {
                        addToDB(Statics.HISTORY, item, 0)
                        setInfo(item, 0)
                    }
                })
                someTask.execute()
            }
        }
    }
    
    @SuppressLint("SetTextI18n")
    private fun setInfo(curItem: ItemMain, position: Int) {
        this.currentItem = curItem
        posOfItem = position
        title = curItem.title[position]
        changeControlIcon()
        img_pb_d.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.loading_anim))
        val loadingAnim = img_pb_d.drawable as AnimationDrawable
        loadingAnim.start()
        if (curItem.img.size > position && !thumbLoaded)
            setPoster(curItem.img[position])

        title_post_detail.text = curItem.title[position]
        count_pages.text = curItem.lang[position].toUpperCase() + ". " +
                curItem.count[position] + " " + getString(R.string.PAGES).toLowerCase()
        artist_post.text = curItem.artists[position].replace(",", ", ")
        parody_post.text = curItem.parodies[position].replace(",", ", ")
        group_post.text = curItem.groups[position].replace(",", ", ")
        tags_post.text = curItem.tags[position].replace(",", ", ")
                .replace("_", " ")
        char_post.text = curItem.characters[position].replace(",", ", ")

        if (content_controls != null) {
            content_controls_fav?.setOnClickListener {
                clickFavor(checkInDB(Statics.FAVORITE))
            }
            content_controls_down?.setOnClickListener {
                Snackbar.make(coordinator_d_layout, getString(R.string.NOT_READY),
                        Snackbar.LENGTH_SHORT).show()
                //clickDownload(checkInDB(Statics.DOWNLOAD))
            }
            changeControlIcon()
        }

        if (artist_post.text.isEmpty() || artist_post.text == " ")
            artist_post_l.visibility = View.GONE
        else artist_post_l.visibility = View.VISIBLE
        artist_post_l.setOnClickListener {
            if (curItem.artistsID.size > 0)
                if (curItem.artistsID[0].isNotEmpty() && curItem.artistsID[0] != " ")
            makeTagDialog(curItem.artists[position], curItem.artistsID[position])
        }

        if (parody_post.text.isEmpty() || parody_post.text == " ")
            parody_post_l.visibility = View.GONE
        else parody_post_l.visibility = View.VISIBLE
        parody_post_l.setOnClickListener {
            if (curItem.parodiesID.size > 0)
                if (curItem.parodiesID[0].isNotEmpty() && curItem.parodiesID[0] != " ")
                makeTagDialog(curItem.parodies[position], curItem.parodiesID[position])
        }

        if (group_post.text.isEmpty() || group_post.text == " ")
            group_post_l.visibility = View.GONE
        else group_post_l.visibility = View.VISIBLE
        group_post_l.setOnClickListener {
            if (curItem.groupsID.size > 0)
                if (curItem.groupsID[0].isNotEmpty() && curItem.groupsID[0] != " ")
                makeTagDialog(curItem.groups[position], curItem.groupsID[position])
        }

        if (tags_post.text.isEmpty() || tags_post.text == " ")
            tags_post_l.visibility = View.GONE
        else tags_post_l.visibility = View.VISIBLE
        tags_post_l.setOnClickListener {
            if (curItem.tagID.size > 0)
                if (curItem.tagID[0].isNotEmpty() && curItem.tagID[0] != " ")
                makeTagDialog(curItem.tags[position].replace(", ", ","),
                        curItem.tagID[position].replace(", ", ","))
        }

        if (char_post.text.isEmpty() || char_post.text == " ")
            char_post_l.visibility = View.GONE
        else char_post_l.visibility = View.VISIBLE
        char_post_l.setOnClickListener {
            if (curItem.charactersID.size > 0)
                if (curItem.charactersID[0].isNotEmpty() && curItem.charactersID[0] != " ")
                makeTagDialog(curItem.characters[position], curItem.charactersID[position])
        }

        if (!curItem.img_list[position].isEmpty() &&
                curItem.img_list[position] != " " &&
                curItem.img_list[position] != curItem.img[position]) {
            setThumbs(curItem, position)
        } else {
            if (!thumbLoaded) {
                thumbLoaded = true
                if (currentUrl.contains("hentai-chan"))
                    onLoad(currentUrl.replace("/manga/", "/online/"), "img")
                if (currentUrl.contains("nude-moon"))
                    onLoad(currentUrl.replace("--", "-online--") + "?row", "img")
            }
        }
    }

    private fun setPoster(s: String) {
        if (s.isEmpty() || s == " ") noImage = true
        var imgUrl = s
        if (currentUrl.contains("nhentai.net"))
            imgUrl = imgUrl.replace("thumb", "1")
                    .replace("t.", "i.")
        if (currentUrl.contains("hentai-chan.me"))
            imgUrl = imgUrl.replace("/showfull_retina/manga/", "/manganew/")
                    .replace("/imgcover.", "/img.")
                    .replace("/manganew_thumbs/", "/manganew/")
        Glide.with(this).load(s).into(img_bar_thumb)
        Glide.with(this).load(imgUrl).into(img_bar)
    }

    private fun setThumbs(curItem: ItemMain, position: Int) {
        if (curItem.img_list.size > 0) {
            var postWidth = 150
            val imgTypes = curItem.img_list[position].split(",")
            val imgLists = ArrayList<String>()

            if (Utils().isTablet(this)) {
                img_bar.setOnClickListener({
                    val imgList = curItem.img_list[position]
                            .replace("/showfull_retina/manga/", "/manganew/")
                            .replace("/imgcover.", "/img.")
                    val intent = Intent(this, ImgActivity::class.java)
                    intent.putExtra("Img", imgList)
                    intent.putExtra("Position", 0)
                    intent.putExtra("Id", position)
                    this.startActivity(intent)
                })
            }

            if (noImage)
                setPoster(imgTypes[0])

            if (content_controls != null)
                postWidth = 180

            for (i in 0 until imgTypes.size) {
                if (i < thumbsCount.toInt()) {
                    var img = imgTypes[i]
                    if (img.contains("hentai-chan.me"))
                        img = img.replace("/manganew_thumbs/", "/manganew/")
                    if (img.endsWith(".jp"))
                        imgLists.add(img + "g")
                    else imgLists.add(img)
                }
            }

            if (thumbsCount != "0") {
                rv_catalog_d.visibility = View.VISIBLE
                read_btn.visibility = View.GONE
                rv_catalog_d.layoutManager = GridLayoutManager(this, Utils().calculateGrid(this, postWidth))
                rv_catalog_d.layoutManager.isAutoMeasureEnabled = true
                val adapter = object : MangaAdapter(this) {
                    override fun open(pos: Int) {
                        val intent = Intent(baseContext, ImgActivity::class.java)
                        Log.d(ContentValues.TAG, "$position")
                        intent.putExtra("Img", curItem.img_list[position])
                        intent.putExtra("Position", pos)
                        intent.putExtra("Id", position)
                        this@DetailActivity.startActivity(intent)
                    }
                }
                rv_catalog_d.isNestedScrollingEnabled = false
                rv_catalog_d.adapter = adapter
                (rv_catalog_d.adapter as MangaAdapter).setImgItems(imgLists)
                rv_catalog_d.adapter.notifyDataSetChanged()
            } else {
                rv_catalog_d.visibility = View.GONE
                read_btn.visibility = View.VISIBLE
                read_btn.setOnClickListener {
                    val intent = Intent(baseContext, ImgActivity::class.java)
                    Log.d(ContentValues.TAG, "$position")
                    intent.putExtra("Img", curItem.img_list[position])
                    intent.putExtra("Position", 0)
                    intent.putExtra("Id", position)
                    this@DetailActivity.startActivity(intent)
                }
            }
        }
    }

    private fun makeTagDialog(title: String, id: String) {
        SearchDialogList.Builder(this, object : OnTaskListener<String> {
            override fun onGetAllItem(items: ArrayList<ItemMain>) {}
            override fun onSuccess(item: ItemMain) {
                Log.d("Detail", "select ${item.tags[0]} id ${item.tagID[0]}")
                val intent = Intent(this@DetailActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.putExtra("Tag", item.tags[0])
                intent.putExtra("ID", item.tagID[0])
                intent.putExtra("Url", currentUrl)
                this@DetailActivity.startActivity(intent)
            }
        }).withTitles(title.split(",").toTypedArray())
                .withIDs(id.split(",").toTypedArray())
                .show()
    }

    private fun removeFromDB(db: String) {
        val dbHelper = DBHelper(this)
        if (checkInDB(db))
            dbHelper.delete(db, title)
    }

    private fun addToDB(db: String, curItem: ItemMain, position: Int) {
        if (curItem.id.size > 0) {
            val dbHelper = DBHelper(this)
            if (checkInDB(db))
                removeFromDB(db)
            dbHelper.insert(db, curItem, position)
        }
    }

    private fun checkInDB(db: String): Boolean {
        return DBHelper(this).getRepeat(db, title)
    }

    private fun clickFavor(isFavor: Boolean) {
        if (isFavor){
            removeFromDB(Statics.FAVORITE)
            Snackbar.make(coordinator_d_layout, getString(R.string.REMOVE), Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.CANCEL), { _ ->
                        addToDB(Statics.FAVORITE, currentItem, posOfItem)
                        changeControlIcon()
                    }).show()
        } else {
            addToDB(Statics.FAVORITE, currentItem, posOfItem)
            Snackbar.make(coordinator_d_layout, getString(R.string.ADDED), Snackbar.LENGTH_SHORT).show()
        }
        changeControlIcon()
    }

    private fun clickDownload(isDownloaded: Boolean) {
        if (isDownloaded) {
            Snackbar.make(coordinator_d_layout, getString(R.string.TO_REMOVE), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.YES), { _ ->
                        removeFromDB(Statics.DOWNLOAD)
                        changeControlIcon()
                    }).show()
        } else {
            addToDB(Statics.DOWNLOAD, currentItem, posOfItem)
            Snackbar.make(coordinator_d_layout, getString(R.string.START_DOWNLOAD), Snackbar.LENGTH_SHORT).show()
        }
        changeControlIcon()
    }

    private fun changeControlIcon (){
        if (content_controls != null) {
            content_controls_fav?.setImageDrawable(AppCompatResources.getDrawable(this,
                    if (checkInDB(Statics.FAVORITE)) R.drawable.ic_menu_fav_true
                    else R.drawable.ic_menu_fav_false))
            content_controls_down?.setImageDrawable(AppCompatResources.getDrawable(this,
                    if (checkInDB(Statics.DOWNLOAD)) R.drawable.ic_menu_down_true
                    else R.drawable.ic_menu_down_false))
        }
        invalidateOptionsMenu()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (content_controls == null) {
            menuInflater.inflate(R.menu.menu_detail, menu)
            menu.findItem(R.id.action_d_fav).icon = AppCompatResources.getDrawable(this,
                    if (checkInDB(Statics.FAVORITE)) R.drawable.ic_menu_fav_true
                    else R.drawable.ic_menu_fav_false)
            menu.findItem(R.id.action_d_down).icon = AppCompatResources.getDrawable(this,
                    if (checkInDB(Statics.DOWNLOAD)) R.drawable.ic_menu_down_true
                    else R.drawable.ic_menu_down_false)
        } else menuInflater.inflate(R.menu.menu_img, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            } R.id.action_d_fav -> {
                clickFavor(checkInDB(Statics.FAVORITE))
                true
            } R.id.action_d_down -> {
                Snackbar.make(coordinator_d_layout, getString(R.string.NOT_READY),
                        Snackbar.LENGTH_SHORT).show()
                //clickDownload(checkInDB(Statics.DOWNLOAD))
                true
            } R.id.action_realod -> {
                noImage = false
                thumbLoaded = false
                onLoad(intent.extras.getString("Url"), "info")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
