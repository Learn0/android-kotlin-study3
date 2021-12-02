package com.sist.kotlinmenuproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_news_site.*

// okHttp => Task (X)  ==> thread
// 갤러리 , GPS (지도)  => 비동기 쓰레드 프로그램
class NewsSiteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_site)

        // site를 받는다
        var site=intent.getStringExtra("site")
        webView.apply {
            settings.javaScriptEnabled=true
            webViewClient= WebViewClient()
            webView.loadUrl(site.toString())
        }
    }
}