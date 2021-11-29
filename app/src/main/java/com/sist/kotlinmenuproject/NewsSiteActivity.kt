package com.sist.kotlinmenuproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_news_site.*
import org.jetbrains.anko.webView

class NewsSiteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_site)
        var site = intent.getStringExtra("site") //request.getParameter("site")
        // 웹뷰 설정 => JavaScript를 사용할 수 있게 설정
        webView.apply{
            settings.javaScriptEnabled=true
            webViewClient= WebViewClient()
        }
        // 형 변환  ==> ?(null을 포함)
        webView.loadUrl(site.toString())
    }
}








