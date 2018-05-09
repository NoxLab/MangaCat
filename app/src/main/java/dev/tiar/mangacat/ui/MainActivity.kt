package dev.tiar.mangacat.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import com.readystatesoftware.systembartint.SystemBarTintManager
import dev.tiar.mangacat.R
import dev.tiar.mangacat.fragments.MainFragment
import dev.tiar.mangacat.items.ItemMain
import dev.tiar.mangacat.items.ItemUrls
import dev.tiar.mangacat.items.Statics
import dev.tiar.mangacat.utils.OnTaskListener
import dev.tiar.mangacat.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*










class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var doubleBackToExitPressedOnce: Boolean = false
    private var catalog = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("fullscreen", false)) {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        catalog = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("start_category", Statics.NHENTAI)

        val prefStart = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("start_category", Statics.NHENTAI)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            this.window.statusBarColor = ContextCompat.getColor(this, R.color.colorBlack)
        } else {
            val tintManager = SystemBarTintManager(this)
            tintManager.isStatusBarTintEnabled = true
            tintManager.setNavigationBarTintEnabled(true)
            tintManager.setTintColor(ContextCompat.getColor(this, R.color.colorBlack))

            tintManager.setNavigationBarTintResource(ContextCompat.getColor(this, R.color.colorBlack))
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.ADDED, R.string.REMOVE)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        var url = ItemUrls.NhentaiHome
        when (prefStart.toLowerCase()) {
            Statics.NHENTAI.toLowerCase() -> url = ItemUrls.NhentaiHome
            Statics.HENTAICHAN.toLowerCase() -> url = ItemUrls.HentaiChanHome
            Statics.NUDENOON.toLowerCase() -> url = ItemUrls.NudeMoonHome
        }
        if (intent.extras.getString("Tag") == "home")
            onPage(url, Statics.CATALOG)
        else if (intent.extras.getString("Url") != null)
            onTag(intent.extras.getString("Url"))
    }

    private fun onTag(url: String) {
        when {
            url.contains("nhentai.net") -> {
                catalog = Statics.NHENTAI
                onPage(ItemUrls.NhentaiTags + intent.extras.getString("ID"),
                        "${Utils().nameFromUrl(ItemUrls.NhentaiTags)}: ${intent.extras.getString("Tag")}")
            }
            url.contains("hentai-chan.me") -> {
                catalog = Statics.HENTAICHAN
                onPage(ItemUrls.HentaiChanTags + intent.extras.getString("ID"),
                        "${Utils().nameFromUrl(ItemUrls.HentaiChanTags)}: ${intent.extras.getString("Tag")}")}
            url.contains("nude-moon.com") -> {
                catalog = Statics.NUDENOON
                onPage(ItemUrls.NudeMoonTags + intent.extras.getString("ID"),
                        "${Utils().nameFromUrl(ItemUrls.NudeMoonTags)}: ${intent.extras.getString("Tag")}")}
        }
    }

    private fun onPage(url: String, category: String) {
        Statics.defaultStatics()

        invalidateOptionsMenu()
        onAttachedToWindow()

        val fragment: Fragment = MainFragment(url)

        if (Utils().isOnline(this)) {
            toolbar.subtitle =
                    if (category == Statics.CATALOG) Utils().nameFromUrl(url)
                    else category
            supportFragmentManager.beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit()
        } else Snackbar.make(main_coordinator_layout, getString(R.string.NO_CONNECT), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.RETRY), { _ ->
                    onPage(url, category)
                }).show()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                return
            }
            this.doubleBackToExitPressedOnce = true
            Snackbar.make(main_coordinator_layout, getString(R.string.PRESS_TO_EXIT), Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.EXIT), { _ ->
                        super.onBackPressed()
                    }).show()

            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val langItem = menu.findItem(R.id.action_lang)
        langItem.isVisible = catalog.toLowerCase() == Statics.NHENTAI.toLowerCase()
        val tagItem = menu.findItem(R.id.action_tags)
        tagItem.isVisible = !(toolbar.subtitle == getString(R.string.nav_favorites) ||
                toolbar.subtitle == getString(R.string.nav_downloads) ||
                toolbar.subtitle == getString(R.string.nav_history))
        val searchView = searchItem?.actionView as SearchView
        searchView.animate()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                when (catalog.toLowerCase()) {
                    Statics.NHENTAI.toLowerCase() ->
                        onPage(ItemUrls.NhentaiSearch + query,
                                "${Utils().nameFromUrl(ItemUrls.NhentaiSearch)}: $query")
                    Statics.HENTAICHAN.toLowerCase() ->
                        onPage(ItemUrls.HentaiChanSearch + query.replace(" ", "+"),
                                "${Utils().nameFromUrl(ItemUrls.HentaiChanSearch)}: $query")
                    Statics.NUDENOON.toLowerCase() ->
                        onPage(ItemUrls.NudeMoonSearch + query.replace(" ", "+"),
                                "${Utils().nameFromUrl(ItemUrls.NudeMoonSearch)}: $query")
                }
                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        return true
    }

    @SuppressLint("ResourceType")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_tags -> {
                var url = ""
                var tags = arrayOf("")
                var id =  arrayOf("")
                when (catalog.toLowerCase()) {
                    Statics.NHENTAI.toLowerCase() -> {
                        url = ItemUrls.NhentaiTags
                        tags = ItemUrls.NhentaiTagsTitle
                        id = ItemUrls.NhentaiTagsID
                    }
                    Statics.HENTAICHAN.toLowerCase() -> {
                        url = ItemUrls.HentaiChanTags
                        tags = ItemUrls.HentaiChanTagsTitle
                        id = ItemUrls.HentaiChanTagsID
                    }
                    Statics.NUDENOON.toLowerCase() -> {
                        url = ItemUrls.NudeMoonTags
                        tags = ItemUrls.NudeMoonTagsTitle
                        id = ItemUrls.NudeMoonTagsID
                    }
                }
                SearchDialogList.Builder(this, object : OnTaskListener<String> {
                    override fun onGetAllItem(items: ArrayList<ItemMain>) {}
                    override fun onSuccess(item: ItemMain) {
                        onPage(url + item.tagID[0], "${Utils().nameFromUrl(url)}: ${item.tags[0]}")
                    }
                }).withTitles(tags)
                        .withIDs(id)
                        .withSearch(true)
                        .show()
                true
            }
            R.id.action_lang -> {
                SearchDialogList.Builder(this, object : OnTaskListener<String> {
                    override fun onGetAllItem(items: ArrayList<ItemMain>) {}
                    override fun onSuccess(item: ItemMain) {
                        onPage(ItemUrls.NhentaiTags + item.tagID[0],
                                "${Utils().nameFromUrl(ItemUrls.NhentaiTags)}: ${item.tags[0]}")
                    }
                }).withTitles(arrayOf("English", "Japanese", "Chinese"))
                        .withIDs(arrayOf("12227", "6346", "29963"))
                        .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        onAttachedToWindow()
        when (item.itemId) {
            R.id.nav_nhentai -> {
                catalog = Statics.NHENTAI
                onPage(ItemUrls.NhentaiHome, Statics.CATALOG)
            }
            R.id.nav_hentaichan -> {
                catalog = Statics.HENTAICHAN
                onPage(ItemUrls.HentaiChanHome, Statics.CATALOG)
            }
            R.id.nav_nudemoon -> {
                catalog = Statics.NUDENOON
                onPage(ItemUrls.NudeMoonHome, Statics.CATALOG)
            }
            R.id.nav_favorite -> {
                onPage(Statics.FAVORITE, "Избранное")
            }
            R.id.nav_history -> {
                onPage(Statics.HISTORY, "История")
            }
            R.id.nav_download -> {
                onPage(Statics.DOWNLOAD, "Сохраненные")
            }
            R.id.nav_settings -> {
                this.startActivity(Intent(this, SettingsActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            }
            R.id.nav_exit -> {
                finish()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onResume() {
        super.onResume()
        onAttachedToWindow()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.isFocusable = true


        val def = HashSet(Arrays.asList(Statics.NHENTAI, Statics.HENTAICHAN, Statics.NUDENOON))
        val prefBase = PreferenceManager.getDefaultSharedPreferences(this)
                .getStringSet("catalog_list", def)

        navigationView.menu.findItem(R.id.nav_nhentai).isVisible = prefBase.contains(Statics.NHENTAI)
        navigationView.menu.findItem(R.id.nav_hentaichan).isVisible = prefBase.contains(Statics.HENTAICHAN)
        navigationView.menu.findItem(R.id.nav_nudemoon).isVisible = prefBase.contains(Statics.NUDENOON)
    }
}
