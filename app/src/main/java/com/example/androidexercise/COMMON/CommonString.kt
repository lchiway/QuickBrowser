package com.example.androidexercise

import android.Manifest

class CommonString{
    companion object {
        private const val MAX_LIST : Int = 1
        var PERMISSION_LIST : Array<String> = Array(MAX_LIST) {Manifest.permission.INTERNET}
    }
}