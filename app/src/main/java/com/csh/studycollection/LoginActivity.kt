package com.csh.studycollection

import android.content.Intent
import android.text.TextUtils
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.LogInListener
import cn.pedant.SweetAlert.SweetAlertDialog
import com.csh.studycollection.base.BaseAppCompatActivity
import com.csh.studycollection.entity.User
import com.csh.studycollection.utils.SHA256Utils
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: BaseAppCompatActivity() {

    override fun provideLayoutRes() = R.layout.activity_login

    override fun onAfterSetContentView() {
        registerTv?.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        loginBtn?.setOnClickListener { doLogin() }
    }

    private fun doLogin() {
        if (TextUtils.isEmpty(edtUsername.text)) {
            toasty("请输入账号")
            return
        }
        if (TextUtils.isEmpty(edtPassword.text)) {
            toasty("请输入密码")
            return
        }
        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.show()
        BmobUser.loginByAccount(
            edtUsername.text.toString(),
            SHA256Utils.SHA256(edtPassword.text.toString()),
            object : LogInListener<User>() {
            override fun done(user: User?, e: BmobException?) {
                pDialog.dismiss()
                if (e == null) {
                    finish()
                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                } else {
                    toasty(e.message)
                }
            }
        })

    }

}