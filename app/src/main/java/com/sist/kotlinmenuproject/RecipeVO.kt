package com.sist.kotlinmenuproject
/*
  NO     NOT NULL NUMBER
TITLE  NOT NULL VARCHAR2(1000)
POSTER NOT NULL VARCHAR2(260)
CHEF   NOT NULL VARCHAR2(200)
LINK            VARCHAR2(260)
HIT             NUMBER
 */
data class RecipeVO(var no:Int,var title:String,var poster:String,var chef:String,
                    var curpage:Int,var totalpage:Int)
