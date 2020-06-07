package com.example.mybook

import java.io.Serializable

data class Book(
    val title:String,
    val author:String,
    val price:Int,
    val discount:Int,
    val publisher:String,
    val pubdate:String,
    val isbn:String,
    val link:String,
    val description:String,
    val imageLink:String
):Serializable{
    init {
        var btitle = title
        var bauthor = author
        var bprice = price
        var bdiscount = discount
        var  bpubli = publisher
        var bpub = pubdate
        var  bisbn = isbn
        var blink = link
        var bdes=description
        var bimg = imageLink
    }
}