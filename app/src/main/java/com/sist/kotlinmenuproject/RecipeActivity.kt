package com.sist.kotlinmenuproject

import android.content.Intent
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
import kotlinx.android.synthetic.main.activity_recipe.*
import okhttp3.*
import java.io.*
import kotlin.concurrent.thread
import java.util.*
import java.net.*

class RecipeActivity : AppCompatActivity() {
    var curpage:Int=1
    var totalpage:Int=0
    // 멤버변수 설정
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)
        RecipeListConnection(this).recipeListData(1)
        recipe_prev.setOnClickListener {
            if(curpage>1)
            {
                curpage--
                RecipeListConnection(this).recipeListData(curpage)
            }
        }
        recipe_next.setOnClickListener {
            if(curpage<totalpage)
            {
                curpage++
                RecipeListConnection(this).recipeListData(curpage)
            }
        }
    }
    // menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId)
        {
            R.id.recipe_list -> {
                var intent= Intent(this,RecipeActivity::class.java)
                startActivity(intent)
            }
            R.id.chef_list -> {
                Toast.makeText(this,"chef-list", Toast.LENGTH_SHORT).show()
            }
            // web : <a> , app: Intent ==> 화면 변경
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
            R.id.food_find -> {
                var intent= Intent(this,FoodFindActivity::class.java)
                // intent에 데이터를 전송할 수 있다
                //intent.putExtra("id","admin")  ?id=admin
                startActivity(intent)
            }
        }
        return false
    }


}
// 스프링 연결
class RecipeListConnection(context:RecipeActivity)
{
    var mContext:RecipeActivity
    init{
        mContext=context
    }
    fun recipeListData(page:Int)
    {
        var strUrl="http://211.238.142.181/web/recipe/kotlin_list.do?page=$page"
        var client=OkHttpClient()
        // 요청 => 쓰레드 (응답값을 받는다)
        var request=Request.Builder().url(strUrl).build() // 연결
        // 프로그램 종료시까지 유지 => 사용하지 않는 경우 => 경고 (socket종료)
        // while(true) ==> 서버와 계속 연결중
        /*
               Thread ==> 모든 프로그램은 프로세스
                          프로세스안에 작업 => 쓰레드 (프로그램안에서 다른 프로그램을 수행)
                          ==> 게임 (자동 움직이게 만든다) 비행기/총알
                          비동기화 (enqueue) / 동기화 (execute)
                          웹서버 => 쓰레드 존재(각자 다른 메뉴를 선택...)
                          서버를 제작 ==> 서버에서 작동하는 서버측 프로그램 제작

         */
        var job=thread{
            client.newCall(request).enqueue(object :Callback{
                // 에러시 처리 (400,404,405,500)
                override fun onFailure(call: Call, e: IOException) {
                    println("Error:${e.message}")
                }
                // 정상 수행시 처리 (200)
                override fun onResponse(call: Call, response: Response) {
                    /*
                           class MyThread implements Runnable
                           {
                              public void run()
                              {
                              }
                           }
                           // 자바
                             1.상속
                               class A extends Thread => 동작이 여러개
                             2.인터페이스
                               class A implements Runnable => 한개일 경우
                             ================윈도우에서 사용
                             웹프로그램 ==> 서버 (이미 만들어져 있다) => 톰캣
                     */
                    mContext.runOnUiThread(Runnable {
                        var recipe_data=response.body?.string().toString() //JSON
                        println("recipe_data:${recipe_data}")
                        var gson=GsonBuilder().setPrettyPrinting().create()
                        // JSON => List로 변경
                        var recipeList:List<RecipeVO> = gson.fromJson(recipe_data,
                            object:TypeToken<List<RecipeVO>>(){}.type
                        )
                        // 확인
                        for(vo in recipeList)
                        {
                            println("번호:${vo.no}")
                            println("레시피명:${vo.title}")
                            println("포스터:${vo.poster}")
                            println("쉐프:${vo.chef}")
                        }

                        //println("현재페이지:${recipeList.get(0).curpage}")
                        //println("총페이지:${recipeList.get(0).totalpage}")
                        mContext.curpage=recipeList.get(0).curpage
                        mContext.totalpage=recipeList.get(0).totalpage
                        var pageView=mContext.findViewById<TextView>(R.id.recipe_page)
                        pageView.text=" ${recipeList.get(0).curpage} page / ${recipeList.get(0).totalpage} pages"
                        mContext.recipe_gridView.adapter=RecipeAdapter(mContext,recipeList)
                    })

                }

            })
        }
    }
}
// row_item연결 => adapter => ListView,GridView,Gallery
class RecipeAdapter(context: RecipeActivity,recipeList:List<RecipeVO>):BaseAdapter(){
        // 멤버변수
        private var mContext:RecipeActivity
        private var recipeList:List<RecipeVO>

        init{
            mContext=context
            this.recipeList=recipeList
        }
        override fun getCount(): Int {
            return recipeList.size
        }

        override fun getItem(position: Int): Any {
            var selectedItem=recipeList.get(position)
            return selectedItem
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            // 화면에 데이터 출력
            var layoutInflater=LayoutInflater.from(mContext)
            var rowMain=layoutInflater.inflate(R.layout.recipe_row_item,parent,false)
            var imageView=rowMain.findViewById<ImageView>(R.id.imageView3)
            var title=rowMain.findViewById<TextView>(R.id.recipe_title)
            var chef=rowMain.findViewById<TextView>(R.id.recipe_chef)

            title.text=recipeList.get(position).title
            chef.text=recipeList.get(position).chef
            Glide.with(rowMain.context).load("${recipeList.get(position).poster}").into(imageView)

            rowMain.setOnClickListener {
                // <a>
                var intent=Intent(mContext,RecipeDetailActivity::class.java)
                intent.putExtra("no", recipeList.get(position).no.toString())
                // ?no=1
                mContext.startActivity(intent)
            }

            return rowMain
        }

}