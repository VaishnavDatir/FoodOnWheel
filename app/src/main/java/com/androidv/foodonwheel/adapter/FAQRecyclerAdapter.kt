package com.androidv.foodonwheel.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidv.foodonwheel.R
import com.androidv.foodonwheel.model.FAQs

data class FAQRecyclerAdapter(val context: Context, val qnaList: ArrayList<FAQs>) :
    RecyclerView.Adapter<FAQRecyclerAdapter.FAQViewHolder>() {

    class FAQViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtCount: TextView = view.findViewById(R.id.txtCount)
        val txtQuestion: TextView = view.findViewById(R.id.txtQuestion)
        val txtAnswer: TextView = view.findViewById(R.id.txtAnswer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_faq_single_row, parent, false)
        return FAQViewHolder(view)
    }

    override fun getItemCount(): Int {
        return qnaList.size
    }

    override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
        val faqs = qnaList[position]
        holder.txtQuestion.text = faqs.question
        holder.txtAnswer.text = faqs.answer
        holder.txtCount.text = (position + 1 ).toString() + "."

    }
}