package www.mrray.cn.loding

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import yee.xin.loading.ILoading

class MainActivity : AppCompatActivity(), ILoading {
    override fun loadData() {
        Log.e("MainActivity", "load")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.yee_xin_loading_activity_main)
    }

}

