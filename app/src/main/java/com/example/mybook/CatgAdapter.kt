package com.example.mybook

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class CatgAdapter(val items: ArrayList<MyCatg>) : RecyclerView.Adapter<CatgAdapter.ViewHolder>() {

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return ViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.catg_row, p0, false))
    }

    override fun getItemCount(): Int {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return items.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        p0.catg_name.text = items.get(p1).toString()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var catg_name:TextView
        init {
            catg_name = itemView.findViewById(R.id.catgName)
            itemView.setOnClickListener {
                val position = adapterPosition
                itemClickListener?.OnItemClick(this, it, items[position], position)
            }

        }
    }

    interface OnItemClickListener{
        fun OnItemClick(holder: ViewHolder, view:View, data:MyCatg, position: Int)
    }

}