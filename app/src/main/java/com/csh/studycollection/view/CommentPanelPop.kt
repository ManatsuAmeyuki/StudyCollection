package com.csh.studycollection.view

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.UpdateListener
import cn.pedant.SweetAlert.SweetAlertDialog
import com.csh.studycollection.LotteryActivity
import com.csh.studycollection.R
import com.csh.studycollection.entity.EventUpdateSettingDisplay
import com.csh.studycollection.entity.Exercise
import com.csh.studycollection.entity.User
import com.hjq.toast.ToastUtils
import com.lxj.xpopup.core.BottomPopupView
import org.greenrobot.eventbus.EventBus


class CommentPanelPop(context: Context, private val exercise: Exercise) : BottomPopupView(context) {

    private var mSubmitBtn: Button? = null
    private var mRgp: RadioGroup? = null
    private var mEdt: EditText? = null

    override fun getImplLayoutId() = R.layout.pop_comment_panel

    override fun onCreate() {
        super.onCreate()
        findViewById<TextView>(R.id.title).text = exercise.topic?.question
        mSubmitBtn = findViewById(R.id.btnSubmit)
        mRgp = findViewById(R.id.llStar)
        mEdt = findViewById(R.id.edt)

        mSubmitBtn?.setOnClickListener { onSubmit() }
        mRgp?.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.rbtn) {

            } else if (checkedId == R.id.rbtnSmall) {

            }
        }
    }

    private fun onSubmit() {
        val comment = mEdt?.text?.toString()
        if (!TextUtils.isEmpty(comment)) {
            updateUserIntegral()
        } else {
            ToastUtils.show("评题才有积分哦~")
        }
    }

    /**
     * 评题加10积分
     */
    private fun updateUserIntegral() {
        val pDialog = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.show()
        val user = BmobUser.getCurrentUser(User::class.java)
        user.integral = (user.integral?:0) + 10
        user.update(object : UpdateListener() {
            override fun done(e: BmobException?) {
                postDelayed({
                    pDialog.dismiss()
                    if (e == null) {
                        SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("评论成功")
                            .setContentText("+10积分")
                            .setConfirmText(" OK ")
                            .showCancelButton(true)
                            .setConfirmClickListener {
                                it.cancel()
                                dismiss()
                            }
                            .setCancelClickListener { sDialog -> sDialog.cancel() }
                            .show()
                    } else {
                        ToastUtils.show("积分获取失败~")
                    }
                }, 500)
            }
        })
    }
}