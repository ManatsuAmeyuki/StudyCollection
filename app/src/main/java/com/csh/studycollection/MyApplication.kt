package com.csh.studycollection

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import cn.bmob.v3.Bmob
import com.hjq.toast.ToastUtils
import com.hjq.toast.style.ToastQQStyle
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy

/**
 * created by shenghuiche on 2019-12-30
 */
class MyApplication: Application() {

    companion object {
        private var instance: MyApplication? = null
            get() {
                if (field == null) {
                    field = MyApplication()
                }
                return field
            }
        @Synchronized
        fun get(): MyApplication{
            return instance!!
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

    override fun onCreate() {
        super.onCreate()

        initBmob()

        initLogger()

        initToast()
    }

    private fun initToast() {
        ToastUtils.init(this)
        ToastUtils.initStyle(ToastQQStyle(this))
    }

    private fun initLogger() {
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
            .methodCount(0)         // (Optional) How many method line to show. Default 2
            .tag(" OKLOG ")   // (Optional) Custom tag for each log. Default PRETTY_LOGGER
            .build()
        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))
    }

    private fun initBmob() {
        //第一：默认初始化
        Bmob.initialize(this, "69690bb9e3a71bb30ad555ad3af0ec44")
        //第二：自v3.4.7版本开始,设置BmobConfig,允许设置请求超时时间、文件分片上传时每片的大小、文件的过期时间(单位为秒)，
        //BmobConfig config =new BmobConfig.Builder(this)
        ////设置appkey
        //.setApplicationId("Your Application ID")
        ////请求超时时间（单位为秒）：默认15s
        //.setConnectTimeout(30)
        ////文件分片上传时每片的大小（单位字节），默认512*1024
        //.setUploadBlockSize(1024*1024)
        ////文件的过期时间(单位为秒)：默认1800s
        //.setFileExpiration(2500)
        //.build();
        //Bmob.initialize(config);
    }

}