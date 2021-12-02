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
import kotlinx.android.synthetic.main.activity_main.*
import java.net.HttpURLConnection
import java.net.*
import java.io.*
// JSON => 해당 데이터형으로 변경
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FoodTask().execute("1")
        // 이벤트 등록
        food_button1.setOnClickListener {
            FoodTask().execute("1")
        }
        food_button2.setOnClickListener {
            FoodTask().execute("2")
        }
        food_button3.setOnClickListener {
            FoodTask().execute("3")
        }

    }
    // 데이터 연결(스프링 서버)
    inner class FoodTask:AsyncTask<String,String,String>(){
        // 서버 연결
        override fun doInBackground(vararg params: String?): String {

            var result:String=""
            // 서버 연결
            // 버튼 클릭시 보내준 데이터 받기
            var no=params[0]
            var url="http://211.238.142.181/web/main/kotlin_main.do?no=$no"

            var obj= URL(url) // URL obj=new URL(url)
            // 서버에서 전송 JSON을 읽어 온다
            with(obj.openConnection() as HttpURLConnection)
            {
                requestMethod="GET" // GetMapping
                BufferedReader(InputStreamReader(inputStream)).use{
                    var response=it.readLine()
                    return response
                }
            }
            return result
        }
        // 실제 데이터 읽기
        override fun onPostExecute(result: String?) {
            println("result:${result}")
            var gson=GsonBuilder().setPrettyPrinting().create()
            var list:List<CategoryVO> = gson.fromJson(result,object:TypeToken<List<CategoryVO>>(){}.type)
            for(vo in list)
            {
                println("번호:${vo.cno}")
                println("제목:${vo.title}")
                println("부제목:${vo.subject}")
                println("포스터:${vo.poster}")
            }
            gridView.adapter=FoodAdapter(this@MainActivity,list)
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
                var intent=Intent(this,RecipeActivity::class.java)
                startActivity(intent)
            }
            R.id.chef_list -> {
                Toast.makeText(this,"chef-list",Toast.LENGTH_SHORT).show()
            }
            // web : <a> , app: Intent ==> 화면 변경
            R.id.food_list -> {
                var intent=Intent(this,MainActivity::class.java)
                startActivity(intent)
            }
            R.id.news -> {
                var intent=Intent(this,NewsActivity::class.java)
                startActivity(intent)
            }
            R.id.chatbot -> {
                Toast.makeText(this,"chatbot",Toast.LENGTH_SHORT).show()
            }
            R.id.food_find -> {
                var intent=Intent(this,FoodFindActivity::class.java)
                // intent에 데이터를 전송할 수 있다
                //intent.putExtra("id","admin")  ?id=admin
                startActivity(intent)
            }
            R.id.seoul_location -> {
                var intent=Intent(this,SeoulLocationActivity::class.java)
                startActivity(intent)
            }
        }
        return false
    }
}
class FoodAdapter(context:Context, cateList:List<CategoryVO>):BaseAdapter()
{
    // 변수 => 초기화 => 1) init , 2) 생성자
    private val mContext:Context
    private val cateList:List<CategoryVO>
    init{
        mContext=context
        this.cateList=cateList
    }
    override fun getCount(): Int {
        return cateList.size
    }

    override fun getItem(position: Int): Any {
        var selectItem=cateList.get(position)
        return selectItem
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // xml => 디자인 읽기
        var layoutInflater=LayoutInflater.from(mContext)
        var rowMain=layoutInflater.inflate(R.layout.food_row_item,parent,false)
        var imgView=rowMain.findViewById<ImageView>(R.id.imageView)
        var food_title=rowMain.findViewById<TextView>(R.id.food_title)
        var food_subject=rowMain.findViewById<TextView>(R.id.food_subject)
        // 디자인 화면 데이터를 출력
        Glide.with(rowMain.context).load("${cateList.get(position).poster}").into(imgView)
        food_title.text=cateList.get(position).title
        food_subject.text=cateList.get(position).subject

        rowMain.setOnClickListener{
            var intent=Intent(rowMain.context,FoodActivity::class.java)
            intent.putExtra("cno",cateList.get(position).cno.toString())
            rowMain.context.startActivity(intent)
            // <a href="">
        }
        return rowMain
    }

}