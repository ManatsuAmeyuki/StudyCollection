package com.csh.studycollection.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hjq.toast.ToastUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


abstract class BaseLazyFragmentV4 : Fragment() {

    protected var rootView: View? = null
    var isFirstLoad: Boolean = true
    var mAct: Activity? = null

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (rootView == null) {
            return
        }
        // 可见，并且没有加载过
        if (isFirstLoad && isVisibleToUser) {
            isFirstLoad = false
            firstLoad()
            return
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mAct = context as Activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (rootView == null)
            rootView = inflater.inflate(provideLayoutRes(), container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // 可见，并且没有加载过
        if (userVisibleHint && isFirstLoad) {
            isFirstLoad = false
            firstLoad()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isFirstLoad) {
            onResumeSafety()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    protected abstract fun onResumeSafety()
    //获取布局文件
    protected abstract fun provideLayoutRes(): Int
    //初始化
    protected abstract fun firstLoad()

    fun toasty(msg: String?) {
        ToastUtils.show(msg)
    }

    fun toasty(resId: Int) {
        ToastUtils.show(resStr(resId))
    }

    fun  resStr(strResId: Int): String = activity?.getString(strResId)?:""

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: String) {}

}