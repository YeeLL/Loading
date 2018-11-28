package www.mrray.cn.loding

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.Toast
import yee.xin.loading.ILoading
import yee.xin.loading.Loading

class TestActivity : Activity(), ILoading {
    override fun loadData() {
        Loading.loading()
        handler.postDelayed({
            handler.sendEmptyMessage(111)
        }, 10 * 1000)
        Toast.makeText(this, "加载成功", Toast.LENGTH_SHORT).show()
    }

    private val handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            Loading.netError()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        Loading.loading()
        handler.postDelayed({
            handler.sendEmptyMessage(111)
        }, 10 * 1000)
    }
}