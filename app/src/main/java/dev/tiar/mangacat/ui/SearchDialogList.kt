package dev.tiar.mangacat.ui

import android.app.Dialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import dev.tiar.mangacat.R
import dev.tiar.mangacat.items.ItemMain
import dev.tiar.mangacat.utils.OnTaskListener
import kotlinx.android.synthetic.main.dialog_list_tags.*


/**
 * Created by Tiar on 03.2018.
 */
class SearchDialogList(context: Context,
                       title: String,
                       titles: Array<String>,
                       ids: Array<String>,
                       callback: OnTaskListener<String>,
                       search: Boolean) : Dialog(context, R.style.yourCustomDialog) {
    internal var adapter: ArrayAdapter<String>? = null
    private var item = ItemMain()
    private val filterTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            adapter!!.filter.filter(s)
        }
    }
    companion object {
        private val TAG = "SearchDialogList"
    }

    init {
        setContentView(R.layout.dialog_list_tags)
        if (title.isNotEmpty())
            this.setTitle(title)
//        mOnItemClickListener = context as OnItemClickListener
        eb_search.addTextChangedListener(filterTextWatcher)
        if (!search) eb_search.visibility = View.GONE
        adapter = ArrayAdapter(context, R.layout.item_text, titles)
        eb_list.adapter = adapter
        eb_list.onItemClickListener = OnItemClickListener { _, _, i, _ ->
            if (titles.size > i && ids.size > i) {
                Log.d(TAG, "Selected tag is = $i ${eb_list.getItemAtPosition(i)}")
                item.tags.add(titles[titles.indexOf(eb_list.getItemAtPosition(i))])
                item.tagID.add(ids[titles.indexOf(eb_list.getItemAtPosition(i))])
                callback.onSuccess(item)
            }
            dismiss()
        }
    }

    override fun onStop() {
        eb_search.removeTextChangedListener(filterTextWatcher)
    }

    class Builder(private var context: Context, private val callback: OnTaskListener<String>) {
        private var titles: Array<String>? = null
        private var ids: Array<String>? = null
        private var search = false
        private var title = ""

        fun withTitles(titles: Array<String>): Builder {
            this.titles = titles
            return this
        }

        fun withTitle(title: String): Builder {
            this.title = title
            return this
        }

        fun withSearch(search: Boolean): Builder {
            this.search = search
            return this
        }

        fun withIDs(ids: Array<String>): Builder {
            this.ids = ids
            return this
        }

        fun show() = SearchDialogList(context, title, titles!!, ids!!, callback, search).show()
    }
}