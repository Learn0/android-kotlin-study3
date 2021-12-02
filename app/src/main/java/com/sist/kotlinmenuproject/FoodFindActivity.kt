package com.sist.kotlinmenuproject

import android.content.Context
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
import kotlinx.android.synthetic.main.activity_food_find.*
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.jsoup.Connection
import java.io.IOException
import kotlin.concurrent.thread

class FoodFindActivity : AppCompatActivity() {
    var curpage:Int=0
    var totalpage:Int=0
    var loc:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_find)

        FoodFindConnection(this).foodFindListData(1, "강남")

        foodFindBtn.setOnClickListener {
            var loc: String = foodFindEdit.text.toString()
            if (loc == "") {
                AlertDialog.Builder(this@FoodFindActivity)
                    .setTitle("경고입니다")
                    .setMessage("찾는 지역을 입력하세요")
                    .setPositiveButton("확인", null)
                    .show()
                foodFindEdit.requestFocus()
            } else {

                FoodFindConnection(this).foodFindListData(1, loc)

            }
        }
        prevBtn.setOnClickListener {
            if(curpage>1)
            {
                curpage--
                FoodFindConnection(this).foodFindListData(curpage, loc)
            }

        }
        nextBtn.setOnClickListener {
            if(curpage<totalpage)
            {
                curpage++
                FoodFindConnection(this).foodFindListData(curpage, loc)
            }
        }
    }
}
class FoodFindConnection(context: FoodFindActivity)
{
        var mContext: FoodFindActivity

        init {
            mContext = context

        }

        // 멤버 함수
        fun foodFindListData(page: Int, fd: String)
        {
            var url = "http://211.238.142.181/web/food/kotlin_find.do?page=${page}&loc=${fd}"
            // 연결
            var client = OkHttpClient()
            var request = Request.Builder().url(url).build()
            // 비동기화 (enqueue()): 동시 (실시간) / 동기화(execute())
            /*
                 모바일 : 안드로이드(코틀린) / 아이폰(스위프트)
                         ================================ React-Native
                          if (response.isSuccessful()) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    //TODO: update your UI
                }

            });
        }
             */
            var job=thread {


                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        // 오류 발생 ==> 500,404,405,403...
                        println("Error:${e.message}") // ex.getMessage()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        // 정상수행시 처리 ==> 200
                        // 값을 받는다
                        mContext.runOnUiThread(Runnable {
                            var food_data = response.body?.string().toString()
                            // find_data=> JSON (스프링에서 보내준 데이터)
                            println("스프링 전송값:${food_data}")
                            var gson = GsonBuilder().setPrettyPrinting().create()
                            var foodList: List<FoodVO> = gson.fromJson(
                                food_data,
                                object : TypeToken<List<FoodVO>>() {}.type
                            )
                            for (vo in foodList) {
                                println("Poster:${vo.poster}")
                                println("Name:${vo.name}")
                                println("================================")
                            }
                            mContext.curpage=foodList.get(0).curpage
                            mContext.totalpage=foodList.get(0).totalpage
                            mContext.loc=foodList.get(0).loc
                            var gridView = mContext.findViewById<GridView>(R.id.loc_gridView)
                            gridView.adapter = FoodFindAdapter(mContext, foodList)

                        })

                        // List,VO변환 ==> gson

                        //food_data=find_data.toString()
                    }

                })
            }
        }

    }
class FoodFindAdapter(context: FoodFindActivity,foodList:List<FoodVO>):BaseAdapter()
{
    private var mContext:FoodFindActivity
    private var foodList:List<FoodVO>

    init{

        mContext=context
        this.foodList=foodList
    }

    override fun getCount(): Int {
         return foodList.size
    }

    override fun getItem(position: Int): Any {
        var selectItem=foodList.get(position)
        return selectItem
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var layoutInflater=LayoutInflater.from(mContext)
        var rowMain=layoutInflater.inflate(R.layout.food_find_row_item,parent,false)
        var nameView=rowMain.findViewById<TextView>(R.id.textView6)
        var posterView=rowMain.findViewById<ImageView>(R.id.imageView3)
        nameView.text=foodList.get(position).name
        Glide.with(rowMain.context).load("${foodList.get(position).poster}").into(posterView)
        return rowMain
    }

}