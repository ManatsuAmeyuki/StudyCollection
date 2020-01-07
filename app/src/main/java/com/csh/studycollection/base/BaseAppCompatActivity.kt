package com.csh.studycollection.base

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.csh.studycollection.entity.ActivityLifeCycleEvent
import com.csh.studycollection.entity.EventFinishAct
import com.hjq.toast.ToastUtils
import io.reactivex.subjects.PublishSubject
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * created by shenghuiche on 2018/12/20
 */
abstract class BaseAppCompatActivity : AppCompatActivity() {

    val mLifeCycleSubj = PublishSubject.create<ActivityLifeCycleEvent>()
    var mContentView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBeforeSetContentView()

        EventBus.getDefault().register(this)
        mLifeCycleSubj.onNext(ActivityLifeCycleEvent.CREATE)
        setContentView(provideLayoutRes())
        mContentView = window.decorView.findViewById(android.R.id.content)

        onAfterSetContentView()
    }

    protected abstract fun provideLayoutRes(): Int

    protected open fun onBeforeSetContentView() {
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去除状态栏
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

//        if (Build.VERSION.SDK_INT >= 28) {
//            val lp = window.attributes
//            lp.layoutInDisplayCutoutMode =
//                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
//            window.attributes = lp
//        }
    }

    protected abstract fun onAfterSetContentView()

    override fun onPause() {
        super.onPause()
        mLifeCycleSubj.onNext(ActivityLifeCycleEvent.PAUSE)
    }

    override fun onStop() {
        super.onStop()
        mLifeCycleSubj.onNext(ActivityLifeCycleEvent.STOP)
    }

    override fun onResume() {
        super.onResume()
        mLifeCycleSubj.onNext(ActivityLifeCycleEvent.RESUME)
    }

    override fun onDestroy() {
        super.onDestroy()
        mLifeCycleSubj.onNext(ActivityLifeCycleEvent.DESTROY)
        EventBus.getDefault().unregister(this)
    }


    fun toasty(msg: String?) {
        ToastUtils.show(msg)
    }

    fun toasty(resId: Int) {
        ToastUtils.show(resStr(resId))
    }

    fun  resStr(strResId: Int): String = getString(strResId)?:""

    fun tipDialog(msg: String) {
        AlertDialog.Builder(this)
                .setTitle("Prompt")
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: String) {}


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFinishActEvent(event: EventFinishAct) {
        if (event.actName == null || event.actName == this.javaClass.name) finish()
    }

}