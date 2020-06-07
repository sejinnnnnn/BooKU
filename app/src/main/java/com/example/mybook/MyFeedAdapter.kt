package com.example.mybook

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.koushikdutta.ion.Ion

class MyFeedAdapter(val feed:ArrayList<MyFeed>)
    : RecyclerView.Adapter<MyFeedAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        var v= LayoutInflater.from(p0.context).inflate(R.layout.card,p0,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return feed.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        if(feed.get(p1).no== -1){//글귀인 경우
            p0.line.visibility = GONE
            p0.author.visibility = GONE
            p0.bname.visibility = GONE
            p0.publisher.visibility = GONE
            p0.contents.text = feed.get(p1).contents
            p0.contents.textSize = 18f
            p0.bimg.visibility= GONE
            p0.like.text = feed.get(p1).bname+"중..."
        }else{
            p0.line.visibility = VISIBLE
            p0.author.visibility = VISIBLE
            p0.bname.visibility = VISIBLE
            p0.publisher.visibility = VISIBLE
            p0.bimg.visibility= VISIBLE
            p0.bname.text=feed.get(p1).bname
            p0.author.text=feed.get(p1).author
            p0.publisher.text=feed.get(p1).publisher
            p0.contents.text=feed.get(p1).contents
            var likeStr = feed.get(p1).like
            if(feed.get(p1).imgsrc==""){
                p0.bimg.visibility = GONE
            }else{
                p0.bimg.visibility = VISIBLE
                Ion.with(p0.bimg).load(feed.get(p1).imgsrc)
            }
            if(likeStr==""){
                p0.like.text = "좋아요 0"
            }else{
                var likeArr = likeStr.split(" ") as ArrayList<String>
                for(i in 0 until likeArr.size){
                    if(likeArr[i]=="")likeArr.removeAt(i)
                }
                p0.like.text = "좋아요 "+likeArr.size
            }
            p0.like.setOnClickListener {
                //좋아요를 클릭했을 때
                if(mainfeed.likeFeed(p1))//등록 성공
                {
                    var likeStr = feed.get(p1).like
                    var likeArr = likeStr.split(" ") as ArrayList<String>
                    for(i in 0 until likeArr.size){
                        if(likeArr[i]=="")likeArr.removeAt(i)
                    }
                    p0.like.text = "좋아요 "+likeArr.size
                }
            }
        }

    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var bimg: ImageView
        var bname: TextView
        var author: TextView
        var publisher: TextView
        var contents: TextView
        var like:TextView
        var line:TextView
        init{
            bimg=itemView.findViewById(R.id.Lbimg)
            bname=itemView.findViewById(R.id.Lbname)
            author=itemView.findViewById(R.id.Lauthor)
            publisher=itemView.findViewById(R.id.Lpublisher)
            contents=itemView.findViewById(R.id.Lcontents)
            like = itemView.findViewById(R.id.likeCount)
            line = itemView.findViewById(R.id.lineText)
        }
    }
}