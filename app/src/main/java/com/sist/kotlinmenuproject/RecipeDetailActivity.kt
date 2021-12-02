package com.sist.kotlinmenuproject

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.IOException
import kotlin.concurrent.thread

class RecipeDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)
        var no=intent.getStringExtra("no")
        var no1:String=no.toString()
        RecipeDetailConnection(this).recipeDetailData(no1)
    }
}
// 서버를 연결해서 데이터 읽기 (OkHttp3)
class RecipeDetailConnection(context: RecipeDetailActivity)
{
    var mContext:RecipeDetailActivity
    init{
        mContext=context
    }
    fun recipeDetailData(no:String)
    {
        // 연결
        var strUrl="http://211.238.142.181/web/recipe/recipe_detail.do?no=$no"
        println("${strUrl}")
        var client=OkHttpClient()
        var request=Request.Builder().url(strUrl).build()
        //var job=thread{
            client.newCall(request).enqueue(object:Callback{
                override fun onFailure(call: Call, e: IOException) {
                    // catch => 404,405, 400 ,500
                    println("Error:${e.message}") // ex.getMessage()
                }

                override fun onResponse(call: Call, response: Response) {
                    // try => 200
                    mContext.runOnUiThread(Runnable {
                        var recipe_detail=response.body?.string()
                        println("서버에서 들어온 값:${recipe_detail}")
                        //var rd=recipe_detail.toString()
                        var gson=GsonBuilder().setPrettyPrinting().create()
                        // VO
                        var vo:RecipeDetailVO = gson.fromJson(
                            recipe_detail,object:TypeToken<RecipeDetailVO>(){}.type
                        )

                        var imageView=mContext.findViewById<ImageView>(R.id.imageView_recipe_detail)
                        var title=mContext.findViewById<TextView>(R.id.recipe_detail_title)
                        var content=mContext.findViewById<TextView>(R.id.recipe_content)
                        var info1=mContext.findViewById<TextView>(R.id.recipe_info1)
                        var info2=mContext.findViewById<TextView>(R.id.recipe_info2)
                        var info3=mContext.findViewById<TextView>(R.id.recipe_info3)

                        // 데이터 출력
                        title.text=vo.title
                        content.text=vo.content
                        info1.text=vo.info1
                        info2.text=vo.info2
                        info3.text=vo.info3
                        Glide.with(mContext).load("${vo.poster}").into(imageView)
                    })
                }

            })
        //}
    }
}











