package com.sist.kotlinmenuproject

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.io.*
import java.net.*
import java.util.*
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_news.*
/*
     스프링 / webView(브라우저 => 사이트)
     URL => HTTP 연결 (보안 정책)
      xml / network-security-config.xml

      AndroidManifest.xml
      android:usesCleartextTraffic="false"
      android:networkSecurityConfig="@xml/network_security_config"
 */
class NewsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        NewsTask().execute("맛집")
        findBtn.setOnClickListener {
            var ss:String=newsFind.text.toString()
            if(ss.equals("")) {
                Toast.makeText(this,"검색어를 입력하세요",Toast.LENGTH_SHORT).show()
                newsFind.requestFocus()
            }
            else
            {
                //NewsTask().execute(ss.toString())*/
                Toast.makeText(this,ss,Toast.LENGTH_SHORT).show()
                NewsTask().execute(ss)
            }

        }
    }
    // spring
    inner class NewsTask:AsyncTask<String,String,String>(){
        override fun doInBackground(vararg params: String?): String {
            var result=""
            var ss=params[0]
            var url="http://211.238.142.181/web/food/news.do?fd=$ss"
            var obj=URL(url)
            /*
                 Java
                   String strurl="http://211.238.142.181/web/news/news_find.do?ss="+ss
                   URL url=new URL(strurl)
                   =var url="http://211.238.142.181/web/news/news_find.do?ss=$ss"
                   =var obj=URL(url)
                   HttpURLConnection conn=(HttpURLConnection)url.openConnection()
                   if(conn!=null)
                   {
                       BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()))
                       while(true)
                       {
                         String msg=in.readLine()
                       }
                   }

             */
            with(obj.openConnection() as HttpURLConnection)
            {
                requestMethod="GET"
                if(responseCode==200)
                {
                    BufferedReader(InputStreamReader(inputStream)).use{
                        var response=it.readLine()

                        return response
                    }
                }
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            val gson= GsonBuilder().setPrettyPrinting().create()
            var newsList:List<NewsVO> = gson.fromJson(result,
                object: TypeToken<List<NewsVO>>(){}.type)
            for(vo in newsList)
            {
                println("제목:${vo.title}")
                println("내용:${vo.description}")
                println("저자:${vo.author}")
                println("링크:${vo.link}")
                println("============================================")
            }
            var listView=findViewById<ListView>(R.id.listView)
            listView.adapter=NewsAdapter(this@NewsActivity,newsList)
        }
    }
}
class NewsAdapter(context: Context, newsList:List<NewsVO>): BaseAdapter(){
    var mContext:Context
    var nList:List<NewsVO>
    init{
        mContext=context
        nList=newsList
    }
    override fun getCount(): Int {
        return nList.size
    }

    override fun getItem(position: Int): Any {
        var selectItem=nList.get(position)
        return selectItem
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var layoutInflater= LayoutInflater.from(mContext)
        val rowMain=layoutInflater.inflate(R.layout.news_row_item,parent,false)
        var nTitle=rowMain.findViewById<TextView>(R.id.news_title)
        var nDesc=rowMain.findViewById<TextView>(R.id.news_desc)
        var nAuthor=rowMain.findViewById<TextView>(R.id.news_author)

        nTitle.text=nList.get(position).title.toString()
        nDesc.text=nList.get(position).description.toString()
        nAuthor.text=nList.get(position).author.toString()
        // <a>
        rowMain.setOnClickListener {
            // 화면 변경 사용
            val intent= Intent(rowMain.context,
                NewsSiteActivity::class.java) // href
            intent.putExtra("site",nList.get(position).link) // ?site=
            rowMain.context.startActivity(intent)
        }
        return rowMain
    }

}
