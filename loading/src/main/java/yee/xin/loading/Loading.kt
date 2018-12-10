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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout

@SuppressLint("StaticFieldLeak")
object Loading : IloadingManager {

    private val mContentViewList: HashMap<Activity, View> by lazy {
        HashMap<Activity, View>()
    }
    private val mCommonViewList: HashMap<Activity, View> by lazy {
        HashMap<Activity, View>()
    }
    private val mErrorViewList: HashMap<Activity, View> by lazy {
        HashMap<Activity, View>()
    }
    private val mIsLoadingList: HashMap<Activity, Boolean> by lazy {
        HashMap<Activity, Boolean>()
    }

    private var mErrorViewLayoutId = R.layout.layout_yee_xin_loading_error
    private var mCommonViewLayoutId = R.layout.layout_yee_xin_loading_common

    private var mAnchorId: Int = 0
    private var mDefaultLoadingStatus = true//默认显示为加载中状态
    private var mActivity: Activity? = null
    private var isBackShow = true

    /**
     * 是否显示返回键  默认返回
     */
    fun setBackShow(backShow: Boolean) {
        this.isBackShow = backShow
    }

    /**
     * 设置锚点  将布局放置在锚点下方
     * @param resId 锚点view ID
     */
    fun setAnchorId(resId: Int): Loading {
        mAnchorId = resId
        return this
    }

    /**
     * 设置自定义的 错误或失败
     * @param errorLayoutId
     */
    fun setErrorViewLayoutId(errorLayoutId: Int) {
        this.mErrorViewLayoutId = errorLayoutId
    }

    /**
     * 自定义的加载view
     * @param commonViewId
     */
    fun setCommonViewLayoutId(commonViewId: Int): Loading {
        this.mCommonViewLayoutId = commonViewId
        return this
    }

    fun init(application: Application): Loading {
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity?) {
            }

            override fun onActivityResumed(activity: Activity?) {
                if (activity != null && activity is ILoading && !mContentViewList.containsKey(activity)) {
                    val contentView: ViewGroup =
                        activity.findViewById<ViewGroup>(android.R.id.content).getChildAt(0) as ViewGroup
                    getLayoutType(contentView, activity)
                }
            }

            override fun onActivityStarted(activity: Activity?) {
            }

            override fun onActivityDestroyed(activity: Activity?) {
                mContentViewList.remove(activity)
                mCommonViewList.remove(activity)
                mErrorViewList.remove(activity)
                mIsLoadingList.remove(activity)
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity?) {
            }

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                mActivity = activity
            }
        })
        return this
    }

    @SuppressLint("InflateParams")
    private fun getLayoutType(contentView: ViewGroup, activity: Activity) {
        initView(activity)
        initClickEvent(activity)

        when (contentView) {
            is RelativeLayout -> {
                contentView.addView(mContentViewList[activity])
                val layoutParams = mContentViewList[activity]!!.layoutParams as RelativeLayout.LayoutParams
                layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT
                layoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT
                if (mAnchorId != 0) {
                    //如果设置了布局 就将布局放置在锚点下方
                    layoutParams.addRule(RelativeLayout.BELOW, mAnchorId)
                }
                mContentViewList[activity]!!.layoutParams = layoutParams
            }
            is LinearLayout -> {
                contentView.addView(mContentViewList[activity], 1)
                val layoutParams = mContentViewList[activity]!!.layoutParams as LinearLayout.LayoutParams
                layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT
                layoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT
                mContentViewList[activity]!!.layoutParams = layoutParams
            }
            is FrameLayout -> {
                Log.e("onActivityResumed", "FrameLayout")
            }
            is ConstraintLayout -> {
                contentView.addView(mContentViewList[activity])
                val layoutParams = mContentViewList[activity]!!.layoutParams as ConstraintLayout.LayoutParams
                layoutParams.bottomToBottom = ConstraintSet.PARENT_ID
                layoutParams.height = 0
                mContentViewList[activity]!!.layoutParams = layoutParams
                if (mAnchorId != 0) {
                    //如果设置了布局 就将布局放置在锚点下方
                    layoutParams.topToBottom = mAnchorId
                }
            }
        }
    }

    private fun initView(activity: Activity) {
        mIsLoadingList[activity] = mDefaultLoadingStatus

        val loadingView =
            LayoutInflater.from(activity).inflate(R.layout.yee_xin_layout_loading, null, false) as ViewGroup
        mContentViewList[activity] = loadingView

        val back = loadingView.findViewById<ImageView>(R.id.yee_xin_loading_back)
        back.visibility = if (isBackShow) View.VISIBLE else View.GONE
        back?.setOnClickListener {
            activity.finish()
        }
        val commonView = LayoutInflater.from(activity).inflate(mCommonViewLayoutId, null, false)
        val loadingCommonContainer = loadingView.findViewById(R.id.yee_xin_loading_common) as ViewGroup
        loadingCommonContainer.addView(commonView)
        setLayoutParams(commonView!!)
        mCommonViewList[activity] = loadingCommonContainer
        loadingCommonContainer.visibility = if (mDefaultLoadingStatus) View.VISIBLE else View.GONE

        val errorView = LayoutInflater.from(activity).inflate(mErrorViewLayoutId, null, false) as ViewGroup
        val loadingErrorContainer = loadingView.findViewById(R.id.yee_xin_loading_error) as ViewGroup
        loadingErrorContainer.addView(errorView)
        setLayoutParams(errorView)
        mErrorViewList[activity] = loadingErrorContainer
    }

    private fun setLayoutParams(view: View) {
        val layoutParams = view.layoutParams
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        view.layoutParams = layoutParams
    }

    /**
     * 设置点击事件
     */
    private fun initClickEvent(activity: Activity) {
        if (activity is ILoading) {
            mContentViewList[activity]!!.setOnClickListener {
                if (!mIsLoadingList[activity]!!) {
                    mIsLoadingList[activity] = true
                    setLoadingVisibility(false)
                    activity.loadData()
                }
            }
        }
    }

    override fun netError() {
        if (mContentViewList[mActivity] != null) {
            mIsLoadingList[mActivity!!] = false
            setLoadingVisibility(true)
        }
    }

    override fun loading() {
        if (mContentViewList[mActivity] != null) {
            mIsLoadingList[mActivity!!] = true
            setLoadingVisibility(false)
        }
    }

    override fun loaded() {
        if (mContentViewList[mActivity] != null) {
            mIsLoadingList[mActivity!!] = false
            mContentViewList[mActivity!!]!!.visibility = View.GONE
        }
    }

    private fun setLoadingVisibility(isError: Boolean) {
        if (mContentViewList[mActivity] == null) {
            return
        }
        mCommonViewList[mActivity]!!.visibility = if (isError) View.GONE else View.VISIBLE
        mErrorViewList[mActivity]!!.visibility = if (!isError) View.GONE else View.VISIBLE
        mContentViewList[mActivity]!!.visibility = View.VISIBLE
    }
}