package dev.tiar.mangacat.ui

import android.content.ContentValues
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import dev.tiar.mangacat.R
import dev.tiar.mangacat.fragments.ImageFragment
import kotlinx.android.synthetic.main.activity_img.*


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class ImgActivity : AppCompatActivity(), ImageFragment.OnButtonClickListener {
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private var images = ""

    override fun getCurPosition(): Int {
        return container_i.currentItem
    }

    override fun hide() {
        // Hide UI first
//        container_i.fullscreen_content.systemUiVisibility =
//                View.SYSTEM_UI_FLAG_LOW_PROFILE or
//                View.SYSTEM_UI_FLAG_FULLSCREEN or
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
//                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
//                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        supportActionBar?.hide()
        prev_button.visibility = View.GONE
        next_button.visibility = View.GONE
    }

    override fun show() {
        // Show the system bar
//        if (!PreferenceManager.getDefaultSharedPreferences(this)
//                        .getBoolean("fullscreen", false)) {
//            container_i.fullscreen_content.systemUiVisibility =
//                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
//                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//        }
        supportActionBar?.show()
        prev_button.visibility = View.VISIBLE
        next_button.visibility = View.VISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (PreferenceManager.getDefaultSharedPreferences(this)
//                        .getBoolean("fullscreen", false)) {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN)
//        }
        setContentView(R.layout.activity_img)
        setSupportActionBar(toolbar_i)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val position = intent.extras.getInt("Position")
        images = intent.extras.getString("Img")
        title = "${position + 1} из ${images.split(",").size}"
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container_i.adapter = mSectionsPagerAdapter
        container_i.currentItem = position

        container_i.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                Log.d(ContentValues.TAG, "onPageSelected()")
            }

            override fun onPageScrollStateChanged(state: Int) {
                title = "${container_i.currentItem + 1} из ${images.split(",").size}"
                Log.d(ContentValues.TAG, "onPageScrollStateChanged()")
            }
        })

        next_button.setOnClickListener { container_i.arrowScroll(View.FOCUS_RIGHT) }
        prev_button.setOnClickListener { container_i.arrowScroll(View.FOCUS_LEFT) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_img, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            } R.id.action_realod -> {
                val curPos = container_i.currentItem

                container_i.adapter = mSectionsPagerAdapter
                container_i.currentItem = curPos
                container_i.adapter?.notifyDataSetChanged()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            Log.d(ContentValues.TAG, "Adapter position: $position of $count")
            return ImageFragment.newInstance(position, images)
        }

        override fun getCount(): Int {
            return images.split(",").size
        }
    }
}
