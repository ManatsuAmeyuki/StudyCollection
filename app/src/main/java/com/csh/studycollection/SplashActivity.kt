package com.csh.studycollection

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.csh.studycollection.service.UserService

/**
 * created by shenghuiche on 2019-12-31
 */
class SplashActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        window.setBackgroundDrawable(null)
        super.onCreate(savedInstanceState)
        Handler().postDelayed({
            // 是否登录
            if (!UserService.hasLogin()) {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            } else {
                // 已经登录则直接进入
                startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
            }
            finish()
        }, 1000)

    }

    override fun onBackPressed() {}

}