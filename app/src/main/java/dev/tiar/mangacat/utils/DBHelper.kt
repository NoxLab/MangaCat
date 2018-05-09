package dev.tiar.mangacat.utils

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import dev.tiar.mangacat.items.ItemMain
import dev.tiar.mangacat.items.Statics


/**
 * Created by Tiar on 03.2018.
 */
class DBHelper(private val context: Context?) : SQLiteOpenHelper(context, "DB", null, 1) {
    private var sqLiteDatabase: SQLiteDatabase? = null
    override fun onCreate(db: SQLiteDatabase) {
        Log.d("mydebug", "--- onCreate database ---")
        db.execSQL("create table ${Statics.FAVORITE} (" +
                "id_db integer primary key autoincrement," +
                "id text," +
                "title text," +
                "count text," +
                "url text," +
                "img text," +
                "imgList text," +
                "tag_url text," +
                "tag_name text," +
                "lang text," +
                "artist text," +
                "parody text," +
                "group_post text," +
                "chara text" +
                ");")
        db.execSQL("create table ${Statics.HISTORY} (" +
                "id_db integer primary key autoincrement," +
                "id text," +
                "title text," +
                "count text," +
                "url text," +
                "img text," +
                "imgList text," +
                "tag_url text," +
                "tag_name text," +
                "lang text," +
                "artist text," +
                "parody text," +
                "group_post text," +
                "chara text" +
                ");")
        db.execSQL("create table ${Statics.DOWNLOAD} (" +
                "id_db integer primary key autoincrement," +
                "id text," +
                "title text," +
                "count text," +
                "url text," +
                "img text," +
                "imgList text," +
                "tag_url text," +
                "tag_name text," +
                "lang text," +
                "artist text," +
                "parody text," +
                "group_post text," +
                "chara text" +
                ");")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    private fun read() {
        sqLiteDatabase = this.readableDatabase
    }

    private fun write() {
        sqLiteDatabase = this.writableDatabase
    }

    fun insert(db: String, cur: ItemMain, position: Int): Long {
        write()
        val cv = ContentValues()
        cv.put("id", cur.id[position])
        cv.put("title", cur.title[position])
        cv.put("count", cur.count[position])
        cv.put("url", cur.url[position])
        cv.put("img", cur.img[position])
        cv.put("imgList", cur.img_list[position])
        cv.put("tag_url", cur.tagID[position])
        cv.put("tag_name", cur.tags[position])
        cv.put("lang", cur.lang[position])
        cv.put("artist", cur.artists[position])
        cv.put("parody", cur.parodies[position])
        cv.put("group_post", cur.groups[position])
        cv.put("chara", cur.characters[position])

        Log.d(TAG, "--- ${cur.title[position]} Insert to $db ---")
        return sqLiteDatabase!!.insert(db, null, cv)
    }

    fun delete(db: String, title: String) {
        write()
        sqLiteDatabase!!.delete(db, "title='$title'", null)
        Log.d(TAG, "--- $title Delete on $db ---")
    }
//
//    fun deleteAll(db: String) {
//        write()
//        sqLiteDatabase!!.delete(db, null, null)
//        Log.d(TAG, "--- Delete $db ---")
//    }
//
//    fun copyDataBaseToSd() {
//        val OUT_PATH = "${Environment.getExternalStorageDirectory()}/" +
//                "${context!!.getString(R.string.app_name)}/${Statics.DB_NAME}"
//        var DB_PATH = context.getDatabasePath(Statics.DB_NAME).path
//        DB_PATH = if (DB_PATH.contains(".sqlite")) DB_PATH.split(".sqlite")[0] else DB_PATH
//        try {
//            val fileExt = File(OUT_PATH)
//            if (!fileExt.exists()) {
//                fileExt.mkdirs()
//            }
//
//            val mInputStream = FileInputStream(DB_PATH)
//            val mOutputStream = FileOutputStream(OUT_PATH + Statics.DB_NAME)
//            val buffer = ByteArray(1024)
//            while (mInputStream.read(buffer) != -1) {
//                mOutputStream.write(buffer, 0, mInputStream.read(buffer))
//            }
//            mInputStream.close()
//            mOutputStream.flush()
//            mOutputStream.close()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//    }
//
//    fun copyDataBaseToData() {
//        val DB_PATH = "${Environment.getExternalStorageDirectory()}/" +
//                "${context!!.getString(R.string.app_name)}/${Statics.DB_NAME}"
//        var OUT_PATH = context.getDatabasePath(Statics.DB_NAME).path
//        OUT_PATH = if (OUT_PATH.contains("DB")) OUT_PATH.split("DB")[0] else OUT_PATH
//        try {
//            val fileExt = File(OUT_PATH)
//            if (!fileExt.exists()) {
//                fileExt.mkdirs()
//            }
//
//            val mInputStream = FileInputStream(DB_PATH)
//            val mOutputStream = FileOutputStream(OUT_PATH + Statics.DB_NAME)
//            val buffer = ByteArray(1024)
//            while (mInputStream.read(buffer) != -1) {
//                mOutputStream.write(buffer, 0, mInputStream.read(buffer))
//            }
//            mInputStream.close()
//            mOutputStream.flush()
//            mOutputStream.close()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//    }

    fun getRepeat(db: String, title: String): Boolean {
        read()
        var r = false
        val cursor = sqLiteDatabase!!.query(db, null, null, null, null, null, null)
        return if (cursor.moveToFirst()) {
            val titleColIndex = cursor.getColumnIndex("title")
            do {
                if (cursor.getString(titleColIndex) == title) {
                    r = true
                    break
                }
            } while (cursor.moveToNext())
            cursor.close()
            r
        } else {
            cursor.close()
            r
        }
    }

    fun getDbItems(db: String): ArrayList<ItemMain> {
        val allItems = ArrayList<ItemMain>()
        val item = ItemMain()
        read()
        val cursor = sqLiteDatabase!!.query(db, null, null, null, null, null, "id_db desc")
        if (cursor.moveToFirst()) {
            val idColIndex = cursor.getColumnIndex("id")
            val titleColIndex = cursor.getColumnIndex("title")
            val countColIndex = cursor.getColumnIndex("count")
            val urlColIndex = cursor.getColumnIndex("url")
            val imgColIndex = cursor.getColumnIndex("img")
            val imgListColIndex = cursor.getColumnIndex("imgList")
            val tagIDColIndex = cursor.getColumnIndex("tag_url")
            val tagsColIndex = cursor.getColumnIndex("tag_name")
            val langColIndex = cursor.getColumnIndex("lang")
            val artistsColIndex = cursor.getColumnIndex("artist")
            val parodiesColIndex = cursor.getColumnIndex("parody")
            val groupPostColIndex = cursor.getColumnIndex("group_post")
            val charactersColIndex = cursor.getColumnIndex("chara")

            do {
                item.id.add(cursor.getString(idColIndex))
                item.title.add(cursor.getString(titleColIndex))
                item.count.add(cursor.getString(countColIndex))
                item.url.add(cursor.getString(urlColIndex))
                item.img.add(cursor.getString(imgColIndex))
                item.img_list.add(cursor.getString(imgListColIndex))
                item.tagID.add(cursor.getString(tagIDColIndex))
                item.tags.add(cursor.getString(tagsColIndex))
                item.lang.add(cursor.getString(langColIndex))
                item.artists.add(cursor.getString(artistsColIndex))
                item.parodies.add(cursor.getString(parodiesColIndex))
                item.groups.add(cursor.getString(groupPostColIndex))
                item.characters.add(cursor.getString(charactersColIndex))

                Log.d(TAG, "ID = " + cursor.getString(idColIndex) + ", title = " + cursor.getString(titleColIndex))
                allItems.add(item)
            } while (cursor.moveToNext())
            cursor.close()
        }
        return allItems
    }
}