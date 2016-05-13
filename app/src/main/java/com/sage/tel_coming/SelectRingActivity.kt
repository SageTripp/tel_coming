package com.sage.tel_coming

import android.media.RingtoneManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.ctx
import org.jetbrains.anko.padding
import org.jetbrains.anko.percent.percentRelativeLayout
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.verticalMargin
import java.net.URL
import java.util.*

/**
 * Created by zhang on 2016/5/11 0011.
 * xxx
 */
class SelectRingActivity : AppCompatActivity() {
    companion object{
        val ID_TOOL = 100001
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ringAdapter = RingAdapter(ArrayList<String>())
        percentRelativeLayout {
            toolbar() {
                id = ID_TOOL
                title = "选择铃声"
                setNavigationOnClickListener {
                    onBackPressed()
                }
            }

            recyclerView {
                layoutManager = LinearLayoutManager(ctx,LinearLayoutManager.VERTICAL,true)
                overScrollMode = View.OVER_SCROLL_NEVER
                adapter = ringAdapter
            }
        }
    }
}