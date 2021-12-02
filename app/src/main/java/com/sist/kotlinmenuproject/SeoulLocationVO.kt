package com.sist.kotlinmenuproject
// Value Object (데이터만 제어하는 클래스)
// Data Transfor Object => 데이터를 전송할 목적의 클래스 (DTO)
data class SeoulLocationVO(var no:Int,var title:String, var poster:String,
              var msg:String,var address:String,
              var curpage:Int,var totalpage:Int)
