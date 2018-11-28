package www.mrray.cn.loding

import android.app.Application
import yee.xin.loading.Loading

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Loading.init(this).setCommonViewLayoutId(R.layout.layout_new_loading)
    }
}