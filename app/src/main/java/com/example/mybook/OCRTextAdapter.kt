package com.example.mybook

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class OCRTextAdapter(val items: MutableList<String>, val clickFun:(String)->Unit)
    :RecyclerView.Adapter<OCRTextAdapter.ViewHolder>(){


    inner class ViewHolder(item: View)
        :RecyclerView.ViewHolder(item){
        val text: TextView = item.findViewById(R.id.card_contents)
    }
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.card_ocr,p0,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size//To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, pos: Int) {
        viewHolder.text.text = items[pos]
        viewHolder.itemView.setOnClickListener {
            clickFun(items[pos])
        }
        //To change body of created functions use File | Settings | File Templates.
    }

}