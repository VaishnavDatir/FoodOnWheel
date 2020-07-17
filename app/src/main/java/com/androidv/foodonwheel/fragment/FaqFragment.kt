package com.androidv.foodonwheel.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidv.foodonwheel.R
import com.androidv.foodonwheel.adapter.FAQRecyclerAdapter
import com.androidv.foodonwheel.model.FAQs

class FaqFragment : Fragment() {

    lateinit var faqRecyclerAdapter: FAQRecyclerAdapter

    lateinit var recyclerFaq: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager

    val faqList = arrayListOf<FAQs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_faq, container, false)

        recyclerFaq = view.findViewById(R.id.recyclerFAQ)
        layoutManager = LinearLayoutManager(activity)

        /*QUESTION 1*/
        val question1 = "What is Food on Wheel?"
        val answer1 =
            "Food on Wheel is a food delivery service that does Home Deliveries to it's customers, " +
                    " We are one of INDIA's premier Home Delivery Service."

        val faq1 = FAQs(question1, answer1)
        faqList.add(faq1)

        /*QUESTION 2*/
        val question2 = "What locations does Food on Wheel service, at the moment?"
        val answer2 =
            "From over 200 restaurants / outlets in Pune, Mumbai, Navi-Mumbai, Nagpur, Amravati, Aurangabad."

        val faq2 = FAQs(question2, answer2)
        faqList.add(faq2)

        /*QUESTION 3*/
        val question3 = "How can you place an order with Food on Wheel™?"
        val answer3 = "You can place an order by using our mobile application."

        val faq3 = FAQs(question3, answer3)
        faqList.add(faq3)

        /*QUESTION 4*/
        val question4 = "Can I order from 2 or more restaurants at the same time?"
        val answer4 =
            "At this moment you cannot place order from two or more restaurants at same time."

        val faq4 = FAQs(question4, answer4)
        faqList.add(faq4)

        /*QUESTION 5*/
        val question5 = "How does Food on Wheel charge you?"
        val answer5 =
            "Food on Wheel charges you only the BILL VALUE - " +
                    "as raised by the Restaurant - and in most cases, DOES NOT charge any delivery charges."

        val faq5 = FAQs(question5, answer5)
        faqList.add(faq5)

        /*QUESTION 6*/
        val question6 = "FORGOT your PASSWORD?"
        val answer6 =
            "DO NOT STRESS - your Username is your Registered Mobile Number. " +
                    "You will get an OTP use that to reset your password. " +
                    " Remember to reset your password, instantly."

        val faq6 = FAQs(question6, answer6)
        faqList.add(faq6)

        faqRecyclerAdapter = FAQRecyclerAdapter(activity as Context, faqList)
        recyclerFaq.adapter = faqRecyclerAdapter
        recyclerFaq.layoutManager = layoutManager
        return view
    }


}