package com.example.androidexercise.MODEL

import android.content.Context
import android.content.SharedPreferences

class UrlModel<K:String, V:Int>() : Model<K, V>  {
    private val SP_NAME:String = "WebBrowser"
    private val URL_COUNT:String = "UrlCount"
    private val URL_NAME:String = "UrlName"

    private var urlSharedPreferences: SharedPreferences? = null
    private lateinit var editor: SharedPreferences.Editor
    private var mContext:Context? = null
    private var urlCount = 0

    constructor(context: Context) : this() {
        mContext = context
        urlSharedPreferences = mContext!!.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        urlCount = urlSharedPreferences!!.getInt(URL_COUNT,0)
    }

    override fun addData(key: K, value: V) {
        editor = urlSharedPreferences!!.edit()
        editor.putString(URL_NAME + value.toString(), key)
        editor.remove(URL_COUNT)
        editor.putInt(URL_COUNT,++urlCount)
        editor.apply()
    }

    override fun modifyData(key: K, value: V) {
        editor = urlSharedPreferences!!.edit()
        editor.remove(URL_NAME+"$value")
        editor.putString(URL_NAME + "$value", key)
        editor.apply()
    }

    override fun deleteData(value: V) {
        editor = urlSharedPreferences!!.edit()
        editor.remove(URL_NAME+"$value")
        editor.putInt(URL_COUNT,--urlCount)
        editor.apply()
    }

    override fun getCount():Int{
        return urlCount
    }

    override fun getData(UrlIndex:V): String {
        return urlSharedPreferences!!.getString(URL_NAME + UrlIndex.toString(), "")!!
    }
}