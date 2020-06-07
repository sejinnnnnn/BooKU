package com.example.mybook

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.koushikdutta.ion.Ion

class sbookAdapter (val sblist:ArrayList<Book>,val click:(Book)->Unit):RecyclerView.Adapter<sbookAdapter.ViewHolder>(){

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): sbookAdapter.ViewHolder {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        var v = LayoutInflater.from(p0.context).inflate(R.layout.sbookcard,p0,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return sblist.size
    }

    override fun onBindViewHolder(p0: sbookAdapter.ViewHolder, p1: Int) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        if(sblist.get(p1).imageLink==""){
            p0.sb_img.visibility= View.INVISIBLE
        }
        else{
            Ion.with(p0.sb_img).load(sblist.get(p1).imageLink)
        }
        p0.itemView.setOnClickListener {
            click(sblist[p1])
        }
        p0.sb_title.text=sblist.get(p1).title
        p0.sb_author.text=sblist.get(p1).author
        p0.sb_publish.text=sblist.get(p1).publisher
    }

    inner class ViewHolder(i: View):RecyclerView.ViewHolder(i){
        var sb_img:ImageView
        var sb_title:TextView
        var sb_author:TextView
        var sb_publish:TextView
        init{
            sb_img=i.findViewById(R.id.s_img)
            sb_title=i.findViewById(R.id.s_bname)
            sb_author=i.findViewById(R.id.s_author)
            sb_publish=i.findViewById(R.id.s_publish)
        }
    }
}