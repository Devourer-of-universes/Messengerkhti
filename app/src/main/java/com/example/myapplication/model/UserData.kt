package com.example.myapplication.model

data class UserData(
    var uid:String = "",
    var name:String = "",
    var phone:String = "Введите телефон",
    var userName:String = "Введите имя",
    var profileImgUrl:String = "",
    var email:String = "",
    var password:String = "",
)