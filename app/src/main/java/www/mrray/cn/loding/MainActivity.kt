package www.mrray.cn.loding

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import yee.xin.loading.ILoading
import yee.xin.loading.Loading

class MainActivity : AppCompatActivity(), ILoading {
    override fun loadData() {
        Log.e("MainActivity", "load")
        Loading.loaded()
//        startActivity(Intent(this@MainActivity, TestActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title_bar.setOnClickListener {
            startActivity(Intent(this@MainActivity, TestActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        Loading.netError()
    }

}

