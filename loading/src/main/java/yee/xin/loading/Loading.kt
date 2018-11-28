package yee.xin.loading

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout


@SuppressLint("StaticFieldLeak")
object Loading : IloadingManager {
    private var mLoadingView: View? = null
    private var mIsLoading = false
    private lateinit var mLoadingCommonView: View
    private lateinit var mLoadingErrorView: View
    private var mTopId: Int = 0

    fun setTopId(resId: Int): Loading {
        mTopId = resId
        return this
    }

    fun init(application: Application): Loading {
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity?) {
            }

            override fun onActivityResumed(activity: Activity?) {
                if (activity != null && activity is ILoading && mLoadingView == null) {
                    val contentView: ViewGroup =
                        activity.findViewById<ViewGroup>(android.R.id.content).getChildAt(0) as ViewGroup
                    getLayoutType(contentView, activity)
                }
            }

            override fun onActivityStarted(activity: Activity?) {
            }

            override fun onActivityDestroyed(activity: Activity?) {
                mLoadingView = null
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity?) {
            }

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {

            }
        })
        return this
    }

    @SuppressLint("InflateParams")
    private fun getLayoutType(contentView: ViewGroup, activity: Activity) {
        mLoadingView = LayoutInflater.from(activity).inflate(R.layout.yee_xin_layout_loading, null, false)
        mLoadingCommonView = mLoadingView!!.findViewById(R.id.yee_xin_loading_common)
        mLoadingErrorView = mLoadingView!!.findViewById(R.id.yee_xin_loading_error)
        when (contentView) {
            is RelativeLayout -> {
                contentView.addView(mLoadingView)
                val layoutParams = mLoadingView!!.layoutParams as RelativeLayout.LayoutParams
                layoutParams.addRule(RelativeLayout.BELOW, mTopId)
                layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT
                layoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT
                mLoadingView!!.layoutParams = layoutParams
            }
            is LinearLayout -> {
                contentView.addView(mLoadingView, 1)
                val layoutParams = mLoadingView!!.layoutParams as LinearLayout.LayoutParams
                layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT
                layoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT
                mLoadingView!!.layoutParams = layoutParams
            }
            is FrameLayout -> {
                Log.e("onActivityResumed", "FrameLayout")
            }
            is ConstraintLayout -> {
                contentView.addView(mLoadingView)
                val layoutParams = mLoadingView!!.layoutParams as ConstraintLayout.LayoutParams
                layoutParams.bottomToBottom = ConstraintSet.PARENT_ID
                layoutParams.height = 0
                mLoadingView!!.layoutParams = layoutParams
                layoutParams.topToBottom = mTopId
            }
        }
        if (activity is ILoading) {
            mLoadingView!!.setOnClickListener {
                if (!mIsLoading) {
                    mIsLoading = true
                    setLoadingVisibility(false)
                    activity.loadData()
                }
            }
        }
    }

    override fun netError() {
        if (mLoadingView != null) {
            mIsLoading = false
            setLoadingVisibility(true)
        }
    }

    override fun loading() {
        if (mLoadingView != null) {
            mIsLoading = true
            setLoadingVisibility(false)
        }
    }

    override fun loaded() {
        if (mLoadingView != null) {
            mIsLoading = false
            mLoadingView!!.visibility = View.GONE
        }
    }

    private fun setLoadingVisibility(isError: Boolean) {
        mLoadingCommonView.visibility = if (isError) View.GONE else View.VISIBLE
        mLoadingErrorView.visibility = if (!isError) View.GONE else View.VISIBLE
        mLoadingView!!.visibility = View.VISIBLE
    }

}