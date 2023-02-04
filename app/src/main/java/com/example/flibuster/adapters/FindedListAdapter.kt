package com.example.flibuster.adapters

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flibuster.R
import com.example.flibuster.api.FlibustaFetch

class FindedListAdapter(private val dataSet: MutableList<Map<String, String>>, private val activity: Activity) :
    RecyclerView.Adapter<FindedListAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bookName: TextView

        init {
            bookName = view.findViewById(R.id.book_name)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_element_layout, viewGroup, false)

        return ViewHolder(view)
    }

    val api = FlibustaFetch()
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        dataSet[position].forEach{
            viewHolder.bookName.text = it.key
            viewHolder.itemView.findViewById<Button>(R.id.fb_download).setOnClickListener{ currentView ->
                api.downloadFb2(it.value, activity)
            }
        }
    }
    override fun getItemCount() = dataSet.size
}