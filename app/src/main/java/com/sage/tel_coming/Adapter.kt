package com.sage.tel_coming

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import org.jetbrains.anko.button
import org.jetbrains.anko.onClick
import org.jetbrains.anko.textView
import org.jetbrains.anko.toast
import java.util.*

/**
 * Created by zhang on 2016/5/11 0011.
 *
 */
class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

class RingAdapter(val list: ArrayList<String> = ArrayList<String>()) : RecyclerView.Adapter<Holder>() {

    lateinit var name: TextView
    lateinit var play: Button
    lateinit var sure: Button

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val ring = list[position]
        name.text = ring
        play.apply {
            onClick {
                text = "正在播放"

            }
        }
        sure.apply {
            onClick {
                context.toast("选择了$ring")
            }
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LinearLayout(parent.context).apply {
            orientation = LinearLayout.HORIZONTAL
            name = textView()
            play = button("试听")
            sure = button("选择")
        })
    }

}