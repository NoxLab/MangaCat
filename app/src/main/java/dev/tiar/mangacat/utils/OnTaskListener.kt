package dev.tiar.mangacat.utils

import dev.tiar.mangacat.items.ItemMain

/**
 * Created by Tiar on 03.2018.
 */
interface OnTaskListener<T> {
    fun onSuccess(item: ItemMain)
    fun onGetAllItem(items: ArrayList<ItemMain>)
//    fun onFailure(e: Exception)
}