package com.example.androidexercise.PRESENTER

interface DataPresenter<K,V>{
    fun addData(key:K, value:V)
    fun modifyData(key:K, value:V)
    fun deleteData(value:V)
    fun getCount():Int
    fun getData(value:V):String
}