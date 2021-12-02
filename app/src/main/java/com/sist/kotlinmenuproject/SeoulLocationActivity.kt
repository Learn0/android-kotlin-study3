package com.sist.kotlinmenuproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_seoul_location.*
import okhttp3.*
import java.io.IOException
import kotlin.concurrent.thread

class SeoulLocationActivity : AppCompatActivity() {
    // 멤버변수
    var curpage:Int=0
    var totalpage:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seoul_location)
        // window.onload
        SeoulLocationConnection(this).seoulLocationListData(1)
        seoul_prev.setOnClickListener {
            if(curpage>1)
            {
                curpage--
                SeoulLocationConnection(this).seoulLocationListData(curpage)
            }
        }
        seoul_next.setOnClickListener {
            if(curpage<totalpage)
            {
                curpage++
                SeoulLocationConnection(this).seoulLocationListData(curpage)
            }
        }
    }
}
// 스프링 연결 => AsyncTask (사용하지 않는다 권고) => OkHttp
// Socket ==> App(WebApp, Game) =>
class SeoulLocationConnection(context:SeoulLocationActivity)
{
    var mContext:SeoulLocationActivity
    // 철저하게 만들어져 있다 => 자바 멤버변수 (자동 초기화 int a => 0) => 초기화
    init{
        mContext=context
    }
    // 연결
    fun seoulLocationListData(page:Int)
    {
        // URL
        var strUrl="http://211.238.142.181/web/seoul/kotlin_location.do?page=$page"
        // Kotlin,Python,VueJS , ReactJS , ES6이상 ==> ;을 사용하지 않는다
        // 자바 (2~3개월 ==> 3년)
        var client=OkHttpClient() // 서버연결
        // 요청
        var request=Request.Builder().url(strUrl).build()
        // Callback () => 시스템에 의해서 자동으로 호출되는 함수
        /*
               main()
               init() , service() , doGet() , doPost()
               자바
               run():쓰레드 동작
               start() ==> 자동으로 run을 호출한다
         */
        //var job=thread{
            // enqueue() : 비동기 => Activity가 쓰레드 => 동시에 수행
            client.newCall(request).enqueue(object:Callback{
                // 비정상적일 경우 => 400(GET/POST) , 405 , 404 , 500
                override fun onFailure(call: Call, e: IOException) {
                    println("Error:${e.message}")
                }
                // 정상수행 => Ajax ==> success:function() => 200
                /*
                     var httpRequest
                     httpRequest.open("POST/GET","URL",true) => 비동기/동기
                     httpRequest.onreadystatecallback=aaa
                     httpRequest.send("id=aaa&pwd=1234")

                     ==> success:function(res){}
                     function aaa(res)
                     {
                        if(httpRequest.state==200)
                        {
                        }
                        else
                        {
                        }
                     }
                 */
                override fun onResponse(call: Call, response: Response) {
                    mContext.runOnUiThread(Runnable {
                        // 값을 받는다
                        var loc_data=response.body?.string().toString()
                        // var name:String
                        // var name:String?=null => null 처리
                        println("스프링에서 받은 값:${loc_data}")
                        // JSON => 해당 데이터형으로 변경 : gson => 자바에서도 사용이 가능
                        var gson=GsonBuilder().setPrettyPrinting().create()
                        var locList:List<SeoulLocationVO> = gson.fromJson(
                            loc_data,
                            object:TypeToken<List<SeoulLocationVO>>(){}.type
                        )
                        // 변경된 내용 출력
                        for(vo in locList)
                        {
                            println("번호:${vo.no}")
                            println("Title:${vo.title}")
                            println("Msg:${vo.msg}")
                            println("Address:${vo.address}")
                            println("Poster:${vo.poster}")
                            println("===================================================")
                        }
                        mContext.curpage=locList.get(0).curpage
                        mContext.totalpage=locList.get(0).totalpage
                        var pagetext=mContext.findViewById<TextView>(R.id.seoul_page)
                        pagetext.text="${mContext.curpage} page / ${mContext.totalpage} pages"
                        var gridView=mContext.findViewById<GridView>(R.id.seoul_gridView)
                        gridView.adapter=SeoulAdapter(mContext,locList)
                    })
                }

            })
        //}
    }
}
// GridView => adapter
class SeoulAdapter(context: SeoulLocationActivity,locList:List<SeoulLocationVO>):BaseAdapter()
{
    var mContext:SeoulLocationActivity
    var locList:List<SeoulLocationVO>

    init{
        mContext=context
        this.locList=locList
    }
    override fun getCount(): Int {
        return locList.size
    }

    override fun getItem(position: Int): Any {
        var selectedItem=locList.get(position)
        return selectedItem
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var layoutInflater=LayoutInflater.from(mContext)
        var rowMain=layoutInflater.inflate(R.layout.seoul_row_item,parent,false)
        var imageView=rowMain.findViewById<ImageView>(R.id.seoul_imageview)
        var title=rowMain.findViewById<TextView>(R.id.seoul_title)
        title.text=locList.get(position).title
        Glide.with(rowMain.context).load("${locList.get(position).poster}")
            .into(imageView)
        rowMain.setOnClickListener {
            var s="${locList.get(position).msg}\n주소:${locList.get(position).address}"
            Toast.makeText(mContext,s,Toast.LENGTH_SHORT).show()
        }
        return rowMain
    }

}