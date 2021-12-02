package com.sist.kotlinmenuproject

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_news.*
import okhttp3.*
import org.jetbrains.anko.internals.AnkoInternals.createAnkoContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class NewsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        //NewsTask().execute("맛집")
        WebConnection(this).newsListData()
        // 버튼 클릭시 처리
        findBtn.setOnClickListener {
            var ss:String = newsFind.text.toString()
            if(ss=="")
            {
                Toast.makeText(this,"검색어를 입력하세요",Toast.LENGTH_SHORT).show()
                //newsFind.requestFocus()

            }
            else
            {
                //NewsTask().execute(ss)
            }
        }
    }
    // Async => 비동기화
    // Activity => Thread ==> 동시 작업을 하기 위해서 비동기화

    inner class NewsTask:AsyncTask<String,String,String>(){
        // 스프링 서버 연결
        override fun doInBackground(vararg params: String?): String {
            var result=""
            var ss=params[0]
            var strUrl="http://211.238.142.181/web/news/kotlin_news_find.do?ss=$ss"
            // URL연결
            var url= URL(strUrl)
            // 데이터 읽기
            with(url.openConnection() as HttpURLConnection){
                requestMethod="GET"
                BufferedReader(InputStreamReader(inputStream)).use{
                    var response=it.readLine()
                    return response
                }
            }
            return result
        }
        // 결과값을 받는다
        override fun onPostExecute(result: String?) {
            println("${result}")
            var gson=GsonBuilder().setPrettyPrinting().create()
            // JSON => 해당 데이터형으로 변환
            var newsList:List<NewsVO> = gson.fromJson(result,
                         object:TypeToken<List<NewsVO>>(){}.type)
            for(vo in newsList)
            {
                println("${vo.title}")
                println("${vo.description}")
                println("${vo.author}")
                println("${vo.link}")
                println("${vo.poster}")
                println("==============================================")
            }
            listView.adapter=NewsAdapter(this@NewsActivity,newsList)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId)
        {
            R.id.recipe_list -> {
                Toast.makeText(this,"recipe-list", Toast.LENGTH_SHORT).show()
            }
            R.id.chef_list -> {
                Toast.makeText(this,"chef-list", Toast.LENGTH_SHORT).show()
            }
            R.id.food_list -> {
                var intent= Intent(this,MainActivity::class.java)
                startActivity(intent)
            }
            R.id.news -> {
                var intent= Intent(this,NewsActivity::class.java)
                startActivity(intent)
            }
            R.id.chatbot -> {
                Toast.makeText(this,"chatbot", Toast.LENGTH_SHORT).show()
            }
        }
        return false
    }
}
// ListView연결 => adapter (화면 출력)
class NewsAdapter(context: Context,newsList:List<NewsVO>):BaseAdapter()
{
    // 멤버변수
    var mContext:Context // NewsActivity
    var newList:List<NewsVO>
    // 멤버변수는 초기화
    init{
        mContext=context  // 변수명이 틀릴 경우에는 this.생략할 수 있다
        this.newList=newsList
    }

    override fun getCount(): Int {
        return newList.size  // 50을 반복한다
    }

    override fun getItem(position: Int): Any {
        var selectItem=newList.get(position)
        return selectItem
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var layoutInflater=LayoutInflater.from(mContext)
        var rowMain=layoutInflater.inflate(R.layout.news_row_item,parent,false)
        var poster=rowMain.findViewById<ImageView>(R.id.imageView4)
        var title=rowMain.findViewById<TextView>(R.id.textView)
        var desc=rowMain.findViewById<TextView>(R.id.textView2)
        var author=rowMain.findViewById<TextView>(R.id.textView3)

        Glide.with(rowMain.context).load("${newList.get(position).poster}").
           into(poster)
        title.text=newList.get(position).title
        desc.text=newList.get(position).description
        author.text=newList.get(position).author

        rowMain.setOnClickListener{
            val intent=Intent(rowMain.context,NewsSiteActivity::class.java)
            intent.putExtra("site",newList.get(position).link)
            // 화면 이동 (변경)
            rowMain.context.startActivity(intent)
        }
        return rowMain
    }

}
class WebConnection(context: NewsActivity){
    private val mContext:NewsActivity
    init{
        mContext=context
    }
    fun newsListData()
    {
        var url="http://211.238.142.181/web/news/kotlin_news_find.do?ss=맛집"
        var client=OkHttpClient()
        var request=Request.Builder().url(url).build()
        val job= thread {
            client.newCall(request).enqueue(object:Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("Error:${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    var news_data=response.body?.string()
                    println("${news_data}")
                    var gson=GsonBuilder().setPrettyPrinting().create()
                    // JSON => 해당 데이터형으로 변환
                    var newsList:List<NewsVO> = gson.fromJson(news_data,
                        object:TypeToken<List<NewsVO>>(){}.type)
                    //listView.adapter=NewsAdapter(mContext,newsList)
                }
            })
        }
    }
}
