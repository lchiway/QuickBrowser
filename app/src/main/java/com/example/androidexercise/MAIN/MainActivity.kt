package com.example.androidexercise

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.androidexercise.PRESENTER.DataPresenter
import com.example.androidexercise.PRESENTER.UrlPresenter
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {
    private val TAG:String = "MainActivity"
    private val PERMISSION_INTERNET_REQUEST_CODE = 1
    private val URL_REQUEST_CODE = 2
    private val URL_MODIFY_CODE = 3
    private val MODIFY_URL = 4
    private val DELETE_URL = 5
    private val URL_NAME:String = "UrlName"

    private var exitTime:Long = 0
    private val backPressMaxTime:Long = 2000
    private var urlCount:Int = 0

    private var urlList: MutableList<String> = ArrayList()
    private var listView:ListView? = null
    private lateinit var floatingActionButton:FloatingActionButton
    private lateinit var urlDataPresenter:DataPresenter<String, Int>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        setContentView(R.layout.activity_main)
        checkPermission()

        urlDataPresenter = UrlPresenter<String, Int>(this)
        listView = findViewById(R.id.web_url_show)
        registerForContextMenu(listView)
        listView?.setOnItemClickListener(){
            _,_,position:Int,_ ->
            val bundle = Bundle()
            val Intent = Intent(this@MainActivity, WebBrowser::class.java)
            bundle.putString(WebBrowser.LOAD_URL, listView?.getItemAtPosition(position).toString())
            Intent.putExtras(bundle)
            startActivity(Intent)
        }
        floatingActionButton = findViewById(R.id.add_button)
        floatingActionButton.setOnClickListener(){
            val intent = Intent(this, UrlEdit::class.java)
            startActivityForResult(intent, URL_REQUEST_CODE)
        }
    }

    override fun onResume(){
        super.onResume()
        init()
        Log.d(TAG, "onResume")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult")
        when(requestCode) {
            URL_REQUEST_CODE -> {
                when(resultCode) {
                    Activity.RESULT_OK -> {
                        var getUrl = data?.extras!!.getString(URL_NAME)!!
                        var returnId = data?.extras!!.getInt("id")
                        when(returnId){
                            -1 -> urlDataPresenter.addData(getUrl, ++urlCount)
                            else -> urlDataPresenter.modifyData(getUrl, returnId)
                        }
                        Log.d(TAG, "get URL: $getUrl : $urlCount")
                    }
                }
            }
        }
    }

    private fun checkPermission(){
        //check permission
        when(ContextCompat.checkSelfPermission(
            this.baseContext,
            Manifest.permission.INTERNET
        )) {
            PackageManager.PERMISSION_DENIED -> {
                ActivityCompat.requestPermissions(this, CommonString.PERMISSION_LIST, PERMISSION_INTERNET_REQUEST_CODE)
            }
            //else -> webView?.loadUrl(LOAD_URL)
        }
    }

    private fun init(){
        var urlName:String? =null

        urlList.clear()
        urlCount = urlDataPresenter.getCount()
        when (urlCount) {
            0 -> urlDataPresenter.addData("https://www.baidu.com", ++urlCount)
        }
        for (i in 1..urlCount) {
            urlName = urlDataPresenter.getData(i)
            urlList.add(urlName!!)
            Log.d(TAG, "init: $urlList : ${urlList.size}")
        }
        listView?.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,urlList)
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo:ContextMenu.ContextMenuInfo ){
        super.onCreateContextMenu(menu, v, menuInfo)
        val info = menuInfo as AdapterView.AdapterContextMenuInfo
        Log.d(TAG, "init: ${info.id}")
        menu.add(0, MODIFY_URL, 0, R.string.modify_key);
        menu.add(0, DELETE_URL, 0, R.string.delete_key);
    }

    override fun onContextItemSelected(item: MenuItem):Boolean{
        val info = item.menuInfo as AdapterContextMenuInfo
        when(item.itemId){
            MODIFY_URL -> {
                modifyUrlData(info.id.toInt()+1)
            }
            DELETE_URL -> {
                urlDataPresenter.deleteData(info.id.toInt()+1)
                init()
            }
        }
        return true
    }

    private fun modifyUrlData(UrlIndex:Int){
        val intent = Intent(this, UrlEdit::class.java)
        val bundle = Bundle()
        bundle.putInt("id", UrlIndex)
        intent.putExtras(bundle)
        startActivityForResult(intent, URL_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PERMISSION_INTERNET_REQUEST_CODE -> {
                when{
                    //agree with the internet process permission
                    grantResults?.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                        return//webView?.loadUrl(LOAD_URL)
                    }
                    //not agree, show the reason why we need the permission
                    else -> {
                        when{
                            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)
                            -> {
                                AlertDialog.Builder(this)
                                    .setMessage(R.string.permission_internet_remind)
                                    .setPositiveButton("OK"){
                                            _, _ ->  ActivityCompat.requestPermissions(this,
                                        CommonString.PERMISSION_LIST, PERMISSION_INTERNET_REQUEST_CODE)
                                    }
                                    .setNegativeButton("Cancel", null)
                                    .create()
                                    .show()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                when {
                    (System.currentTimeMillis() - exitTime) > backPressMaxTime -> {
                        Toast.makeText(
                            applicationContext,
                            "Press again to exit!",
                            Toast.LENGTH_SHORT
                        ).show()
                        exitTime = System.currentTimeMillis()
                    }
                    else -> finish()
                }
            }
        }
        return true
    }
}
