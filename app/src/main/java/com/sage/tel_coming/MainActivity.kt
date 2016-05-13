package com.sage.tel_coming

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import org.jetbrains.anko.UI
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.button
import org.jetbrains.anko.verticalLayout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UI {
            verticalLayout {
                button ("选择铃声") {
                    backgroundColor = R.color.colorAccent
                }
                button("播放模式") {
                    backgroundColor = R.color.accent_material_light
                }
            }
        }
    }
}
