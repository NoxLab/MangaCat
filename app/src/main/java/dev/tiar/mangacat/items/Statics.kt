package dev.tiar.mangacat.items

/**
 * Created by Tiar on 03.2018.
 */
class Statics {
    companion object {
        var cur_items = 0
        var isLoading = false
        var item_cur = ItemMain()
        var curTag = ""
        var curTagID = ""

        const val GRID = "grid_catalog"
        const val CATALOG = "catalog"
        const val HISTORY = "history"
        const val FAVORITE = "favorite"
        const val DOWNLOAD = "download"
        const val NHENTAI = "NHentai"
        const val HENTAICHAN = "HentaiChan"
        const val NUDENOON = "NudeMoon"

        fun defaultStatics() {
            item_cur = ItemMain()
            cur_items = 0
            curTag = ""
            curTagID = ""
        }
    }
}