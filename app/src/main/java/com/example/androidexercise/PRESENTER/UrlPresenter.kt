package com.example.androidexercise.PRESENTER

import android.content.Context
import com.example.androidexercise.MODEL.*

class UrlPresenter<K:String, V:Int>() : DataPresenter<K, V>{
    private var model: Model<String, Int>? = null

    constructor(context: Context) : this() {
        model = UrlModel(context)
    }

    override fun addData(key: K, value: V) {
        model!!.addData(key,value)
    }

    override fun modifyData(key: K, value: V) {
        model!!.modifyData(key,value)
    }

    override fun deleteData(value: V) {
        model!!.deleteData(value)
    }

    override fun getCount():Int{
        return model!!.getCount()
    }

    override fun getData(value:V): String{
        return model!!.getData(value)
    }
}