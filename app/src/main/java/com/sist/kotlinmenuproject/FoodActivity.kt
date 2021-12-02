package com.sist.kotlinmenuproject

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_food.*
import java.util.*
import java.io.*
import java.net.*
/*
     inner class => 멤버 클래스
     class A
     {
         A클래스가 가지고 있는 모든 데이터,메소드를 사용 가능 (A,B 공유하는 데이터가 있는 경우)
             ==> 네트워크 , 쓰레드
         class B
         {
         }
     }

     inner class => 지역 클래스 (거의 사용 빈도가 없다)
     class A
     {
        public void display()
        {
            class B
            {
            }
        }
     }

     inner class => 익명의 클래스 (상속없이 오버라이딩이 가능) => Window
     class A
     {
          B b=new B()
          {
              public void display()
              {
              }
          }
     }
     class B
     {
        public void display()
        {
        }
     }
 */
class FoodActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food)
        //  intent.putExtra("cno",cateList.get(position).cno.toString())
        var cno=intent.getStringExtra("cno")
        CateFoodTask().execute(cno)

    }
    // ~VO , JSON=>변환 , Adapter연결
    // 스프링 연결
    inner class CateFoodTask:AsyncTask<String,String,String>(){
        override fun doInBackground(vararg params: String?): String {
            // vararg => 가변 매개변수 ==> Object...,String...
            var result=""
            var cno=params[0]
            var strUrl="http://211.238.142.181/web/food/kotlin_category_list.do?cno=$cno"
            var url=URL(strUrl)
            // 연결하고 데이터 읽기  ==> openConnection()
            with(url.openConnection() as HttpURLConnection){
                requestMethod="GET"
                BufferedReader(InputStreamReader(inputStream)).use{
                    var response=it.readLine()
                    return response
                }
            }
            return result
        }

        override fun onPostExecute(result: String?) {
             // Adapter 연결 ==> 결과값을 읽어 온다
             // JSON파싱 => gson
            var gson=GsonBuilder().setPrettyPrinting().create()
            var list:List<FoodVO> = gson.fromJson(result,object:TypeToken<List<FoodVO>>(){}.type)
            /// 데이터 출력
            val food_title=findViewById<TextView>(R.id.textView4)
            val food_subject=findViewById<TextView>(R.id.textView5)
            food_title.text=list.get(0).title
            food_subject.text=list.get(0).subject
            println("title:${list.get(0).title}")
            println("subject:${list.get(0).subject}")
            food_listView.adapter=CateFoodAdapter(this@FoodActivity, list )

        }
    }
}
// Adater => GridView , ListView
class CateFoodAdapter(context: Context,foodList:List<FoodVO>):BaseAdapter()
{
    var mContext:Context
    var foodList:List<FoodVO>

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
        // XML을  읽어 온다 => 배치 (XML로 UI)
        var layoutInflater=LayoutInflater.from(mContext) //mContext=MainActivity
        var rowMain=layoutInflater.inflate(R.layout.cate_food_row,parent,false)
        var imageView=rowMain.findViewById<ImageView>(R.id.imageView2)
        var name=rowMain.findViewById<TextView>(R.id.cate_title)
        var addr=rowMain.findViewById<TextView>(R.id.cate_addr)
        var tel=rowMain.findViewById<TextView>(R.id.cate_tel)

        name.text=foodList.get(position).name
        addr.text=foodList.get(position).address
        tel.text=foodList.get(position).tel

        Glide.with(rowMain.context).load("${foodList.get(position).poster}")
            .into(imageView)
        return rowMain
    }

}