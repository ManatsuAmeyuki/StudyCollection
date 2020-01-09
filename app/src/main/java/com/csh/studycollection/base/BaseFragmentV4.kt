package com.csh.studycollection.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.hjq.toast.ToastUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


abstract class BaseFragmentV4: androidx.fragment.app.Fragment() {

    var mAct: Activity? = null
    var mContentView: View? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mAct = context as Activity? // 保存Context引用
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layoutRes = provideLayoutRes()
        if (layoutRes == 0) {
            return super.onCreateView(inflater, container, savedInstanceState)
        } else {
            mContentView = inflater.inflate(layoutRes, container, false)
            return mContentView
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        onAfterActivityCreated()
    }

    protected abstract fun provideLayoutRes(): Int

    protected abstract fun onAfterActivityCreated()

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    fun toasty(msg: String?) {
        ToastUtils.show(msg)
    }

    fun tipDialog(msg: String) {
        if (activity != null && isAdded) {
            AlertDialog.Builder(activity!!)
                    .setTitle("Prompt")
                    .setMessage(msg)
                    .setPositiveButton("OK", null)
                    .show()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: String) {}

}
