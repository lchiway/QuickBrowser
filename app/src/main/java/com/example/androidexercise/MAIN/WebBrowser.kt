package com.example.androidexercise

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WebBrowser : AppCompatActivity() {
    //view
    private var webView: WebView? = null
    private var titleView: TextView? = null
    private var loadStatus: TextView? = null
    private var beginLoading: TextView? = null
    private var loadUrl:String = ""

    companion object {
        val LOAD_URL: String = "LOAD_URL"
        private val TAG:String = "WebBrowser"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.web_browser_layout)
        loadUrl = getIntentUrl();
        when(loadUrl.contains("https://")){
            false -> loadUrl = "https://$loadUrl"
        }
        Log.d(TAG,"get load Url from main: $loadUrl")
    }

    override fun onResume(){
        super.onResume()
        init()
    }

    override fun onDestroy() {
        super.onDestroy()
        webView?.loadDataWithBaseURL(null,"","text/html","utf-8",null)
        webView?.clearHistory()
        (webView?.parent as ViewGroup)?.removeView(webView)
        webView?.destroy()
        webView = null
    }

    private fun init(){
        titleView = findViewById(R.id.title)
        loadStatus = findViewById(R.id.text_load_status)
        beginLoading = findViewById(R.id.text_Loading)
        webView = findViewById(R.id.webView)
        webView?.loadUrl(loadUrl)
        webView?.webViewClient = (object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                loadUrl = url
                view.loadUrl(loadUrl)
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                loadStatus?.text = "Loading"
                beginLoading?.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                loadStatus?.text = "Load done"
                beginLoading?.visibility = View.GONE
            }
        })
        webView?.webChromeClient = (object : WebChromeClient(){
            override fun onReceivedTitle(view: WebView?, title: String?) {
                titleView?.text = title
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                when{
                    newProgress <= 100 -> {
                        var progress:String = "$newProgress%"
                        beginLoading?.text = progress
                    }
                }
            }
        })
        var webSettings: WebSettings? = webView?.settings
        webSettings?.useWideViewPort = true
        webSettings?.loadWithOverviewMode = true
        webSettings?.builtInZoomControls = true
        webSettings?.displayZoomControls = false
        webSettings?.setSupportZoom(true)
        webSettings?.javaScriptEnabled = true
        webSettings?.loadsImagesAutomatically = true
    }

    private fun getIntentUrl():String{
        return this.intent.getStringExtra(LOAD_URL)!!
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        when(keyCode){
            KeyEvent.KEYCODE_BACK -> {
                when{
                    webView!!.canGoBack() -> webView!!.goBack()
                    else -> finish()
                }
            }
        }
        return true
    }
}