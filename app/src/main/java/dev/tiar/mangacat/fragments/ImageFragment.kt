package dev.tiar.mangacat.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.ImageLoader
import com.github.piasy.biv.loader.glide.GlideImageLoader
import dev.tiar.mangacat.R
import dev.tiar.mangacat.utils.MemoryCache
import dev.tiar.mangacat.utils.Utils
import kotlinx.android.synthetic.main.fragment_img.view.*
import java.io.File
import java.lang.Exception


/**
 * Created by Tiar on 03.2018.
 */
class ImageFragment : Fragment() {
    private val TAG = "ImageFragment"
//    private var idPost: Int = 0
    private var images = ""
    private var positionPost: Int = 0
    private var mVisible: Boolean = false
//    private var curItem = ItemMain()
    private var mOnButtonClickListener: OnButtonClickListener? = null

    internal interface OnButtonClickListener {
        fun getCurPosition() : Int
        fun hide()
        fun show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        BigImageViewer.initialize(GlideImageLoader.with(context))
        val rootView = inflater.inflate(R.layout.fragment_img, container, false)
        mOnButtonClickListener = context as OnButtonClickListener

        images = arguments!!.getString(ARG_SECTION_IMG)
        positionPost = arguments!!.getInt(ARG_SECTION_NUMBER)

        var imgUrl = images.split(",")[positionPost]
        if (imgUrl.contains("nhentai.net"))
            imgUrl = imgUrl.replace("/t.", "/i.").replace("t.", ".")
        if (imgUrl.contains("hentai-chan.me"))
            imgUrl = imgUrl.replace("/manganew_thumbs/", "/manganew/")
        if (imgUrl.endsWith(".jp"))
            imgUrl += "g"

        Log.d(TAG, "Num: $positionPost of: ${images.split(",").size} img: $imgUrl")

        // Set up the user interaction to manually show or hide the system UI.
        rootView.fullscreen_content.setOnClickListener {
            toggle()
        }
        onLoadImage(imgUrl, rootView)
        return rootView
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume()")
    }

    private fun toggle() {
        Log.d(TAG, "Full screen $mVisible")
        mVisible = !mVisible
        if (mVisible) mOnButtonClickListener!!.hide()
        else mOnButtonClickListener!!.show()
    }

    private fun onLoadImage(img_url: String, view: View) {
        view.img_pb_i.setImageDrawable(ContextCompat.getDrawable(this.context!!, R.drawable.loading_anim))
        val loadingAnim = view.img_pb_i.drawable as AnimationDrawable
        loadingAnim.start()
        view.img_pb_i.visibility = View.VISIBLE
        view.text_pb_i.visibility = View.VISIBLE

        if (Utils().isOnline(context as Activity)) {
            val imgCache = images.split(",")[positionPost]
            Glide.with(context!!).load(imgCache).into(view.fullscreen_content_thumb)
            val myImageLoaderCallback = object : ImageLoader.Callback {
                override fun onFail(error: Exception?) {
                    view.text_pb_i.visibility = View.VISIBLE
                    view.text_pb_i.text = "Error..."
                    view.img_pb_i.visibility = View.GONE

                    Log.d(TAG, "error $img_url")
                }

                override fun onFinish() {
                    Log.d(TAG, "loaded $img_url")
                }

                override fun onSuccess(image: File?) {
                    view.img_pb_i.visibility = View.GONE
                    view.text_pb_i.visibility = View.GONE
                }

                override fun onCacheHit(image: File?) {
                    Log.d(MemoryCache.TAG, "cached $img_url $image")
                }

                override fun onCacheMiss(image: File?) {
                }

                @SuppressLint("SetTextI18n")
                override fun onProgress(progress: Int) {
                    view.text_pb_i.text = "$progress%"
                }

                override fun onStart() {
                }
            }
            view.fullscreen_content.setImageLoaderCallback(myImageLoaderCallback)
            view.fullscreen_content.showImage(Uri.parse(img_url))
        } else Snackbar.make(view.constraintLayout, getString(R.string.NO_CONNECT), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.RETRY), { _ ->
                    onLoadImage(img_url, view)
                }).show()
    }

    companion object {
        private const val ARG_SECTION_NUMBER = "section_position"
        private const val ARG_SECTION_IMG = "section_id"
        fun newInstance(position: Int, img: String): ImageFragment {
            val fragment = ImageFragment()
            val args = Bundle()
            args.putInt(ARG_SECTION_NUMBER, position)
            args.putString(ARG_SECTION_IMG, img)
            fragment.arguments = args
            return fragment
        }
    }
}
