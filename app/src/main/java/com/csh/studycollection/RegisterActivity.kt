package com.csh.studycollection

import android.content.Intent
import android.graphics.Color
import android.text.TextUtils
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import cn.pedant.SweetAlert.SweetAlertDialog
import com.csh.studycollection.base.BaseAppCompatActivity
import com.csh.studycollection.entity.User
import com.csh.studycollection.utils.SHA256Utils
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity: BaseAppCompatActivity() {

    override fun provideLayoutRes() = R.layout.activity_register

    override fun onAfterSetContentView() {
        backTv?.setOnClickListener { finish() }
        signupBtn?.setOnClickListener { doSignUp() }
    }

    /**
     * 注册
     */
    private fun doSignUp() {
        if (TextUtils.isEmpty(edtUsername.text)) {
            toasty("请输入账号")
            return
        }
        if (TextUtils.isEmpty(edtPassword.text)) {
            toasty("请输入密码")
            return
        }
        if (TextUtils.isEmpty(edtPassword2.text)) {
            toasty("请再次输入密码")
            return
        }
        if (!TextUtils.equals(edtPassword.text.toString(), edtPassword2.text.toString())) {
            toasty("两次输入的密码不一致")
            return
        }

        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.show()
        val user = User()
        user.username = edtUsername.text.toString()
        user.setPassword(SHA256Utils.SHA256(edtPassword.text.toString()))
        user.signUp(object : SaveListener<User>() {
            override fun done(user: User?, e: BmobException?) {
                pDialog.dismiss()
                if (e == null) {
                    toasty("注册成功~")
                    finish()
                    startActivity(Intent(this@RegisterActivity, HomeActivity::class.java))
                } else {
                    toasty(e.message)
                }
            }
        })
    }

}