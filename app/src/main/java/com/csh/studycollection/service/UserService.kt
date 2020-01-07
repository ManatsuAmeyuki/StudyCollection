package com.csh.studycollection.service

import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FetchUserInfoListener
import cn.bmob.v3.listener.UpdateListener
import com.csh.studycollection.entity.User


/**
 * created by shenghuiche on 2019-12-30
 */
object UserService {

    /**
     * 判断当前是否有用户登录
     */
    fun hasLogin() = BmobUser.isLogin()

    /**
     * 获取当前登录的用户的信息
     */
    fun getCurrentLoginUser(): User? {
        if (BmobUser.isLogin()) {
            return BmobUser.getCurrentUser(User::class.java)
        }
        return null
    }

    /**
     * 同步控制台数据到缓存中
     */
    fun fetchUserInfo() {
        BmobUser.fetchUserInfo(object : FetchUserInfoListener<BmobUser>() {
            override fun done(user: BmobUser, e: BmobException?) {
                if (e == null) {
                    val myUser = BmobUser.getCurrentUser<User>(User::class.java)
//                    Snackbar.make(
//                        view,
//                        "更新用户本地缓存信息成功：" + myUser.getUsername() + "-" + myUser.getAge(),
//                        Snackbar.LENGTH_LONG
//                    ).show()
                } else {
//                    Log.e("error", e.message)
//                    Snackbar.make(view, "更新用户本地缓存信息失败：" + e.message, Snackbar.LENGTH_LONG).show()
                }
            }
        })
    }

    /**
     * 获取控制台最新JSON数据(不同步到缓存中)
     */
    private fun fetchUserJsonInfo() {
        BmobUser.fetchUserJsonInfo(object : FetchUserInfoListener<String>() {
            override fun done(json: String, e: BmobException?) {
                if (e == null) {
//                    Log.e("success", json)
//                    Snackbar.make(view, "获取控制台最新数据成功：$json", Snackbar.LENGTH_LONG).show()
                } else {
//                    Log.e("error", e.message)
//                    Snackbar.make(view, "获取控制台最新数据失败：" + e.message, Snackbar.LENGTH_LONG).show()
                }
            }
        })
    }

    /**
     * 提供旧密码修改密码
     */
    fun updatePassword(oldP: String, newP: String) {
        BmobUser.updateCurrentUserPassword(oldP, newP, object : UpdateListener() {
            override fun done(e: BmobException?) {
                if (e == null) {
//                    Snackbar.make(view, "查询成功", Snackbar.LENGTH_LONG).show()
                } else {
//                    Snackbar.make(view, "查询失败：" + e.message, Snackbar.LENGTH_LONG).show()
                }
            }
        })
    }


    /**
     * 退出登录，同时清除缓存用户对象
     */
    fun logout () {
        BmobUser.logOut()
    }

}