package com.example.transformsaction

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.example.transformsaction.annotation.CheckViewOnClick
import com.example.transformsaction.annotation.UncheckViewOnClick
import com.example.transformsaction.ui.theme.TransFormsActionTheme
import com.example.transformsaction.view.ViewDoubleClickAdapter

class MainActivity : AppCompatActivity() ,OnClickListener{
    private var clickIndex = 1

    @Suppress("ObjectLiteralToLambda")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_double_click_check)
        title = "View 双击防抖"

        findViewById<ImageView>(R.id.imageTest).setOnClickListener{
            findViewById<ImageView>(R.id.imageTest).setImageDrawable(ContextCompat.getDrawable(this,
                com.chad.library.R.drawable.notification_bg_low))
        }

        findViewById<Button>(R.id.bvObjectInterface).setOnClickListener(this)

        findViewById<TextView>(R.id.tvObjectUnCheck).setOnClickListener(object :
            View.OnClickListener {
            @UncheckViewOnClick
            override fun onClick(view: View) {
                onClickView()
            }
        })
        findViewById<TextView>(R.id.tvObjectCheck).setOnClickListener(object :
            View.OnClickListener {
            override fun onClick(view: View) {
                onClickView()
            }
        })
        findViewById<TextView>(R.id.tvLambda).setOnClickListener {
            onClickView()
        }
        val viewDoubleClickAdapter = ViewDoubleClickAdapter()
        viewDoubleClickAdapter.addChildClickViewIds(R.id.viewChild)
        viewDoubleClickAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
                Thread.sleep(100)
                onClickView()
            }
        })
        viewDoubleClickAdapter.setOnItemChildClickListener(object : OnItemChildClickListener {
            override fun onItemChildClick(
                adapter: BaseQuickAdapter<*, *>,
                view: View,
                position: Int
            ) {
                Thread.sleep(100)
                if (view.id == R.id.viewChild) {
                    onClickView()
                }
            }
        })
        val rvList = findViewById<RecyclerView>(R.id.rvList)
        rvList.adapter = viewDoubleClickAdapter
        rvList.layoutManager = LinearLayoutManager(this)
    }

    @CheckViewOnClick
    fun onClickByXml(view: View) {
        onClickView()
    }

    private fun onClickView() {
        findViewById<TextView>(R.id.tvIndex).text = (clickIndex++).toString()
    }

    override fun onClick(v: View?) {
        onClickView()
    }

}
