package com.csh.studycollection.view

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.widget.ImageView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.csh.studycollection.R
import com.csh.studycollection.entity.EventCard
import com.lxj.xpopup.core.CenterPopupView
import org.greenrobot.eventbus.EventBus
import java.util.*


class LotteryAnimationPop(context: Context, private val consume: Int) : CenterPopupView(context) {

    private var iv: ImageView? = null
    private var animationDrawable:AnimationDrawable? = null

    override fun getImplLayoutId(): Int {
        return R.layout.pop_lottery_animation
    }

    override fun onCreate() {
        super.onCreate()
        iv = findViewById(R.id.iv)
        animationDrawable = iv?.background as AnimationDrawable
        animationDrawable?.start()
        iv?.postDelayed({
            if (animationDrawable?.isRunning == true) {
                animationDrawable?.stop()
            }
            randomCard()
        }, 2000)
    }

    private fun randomCard() {
        val r = Random().nextInt(100)
        var res = -1 // 结果数字
        if (r < 60){ // 60%的几率 不中奖
            res = -1
        } else if(r < 90){ // [60,90)，30个数字的区间，30%的几率 中奖1
            res = 1
        } else if (r < 98){ // 8%的几率 中奖2
            res = 2
        } else { // 2%的几率 中奖3
            res = 3
        }

        if (res == -1) {
            SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("手气不佳")
                .setContentText("很遗憾未能抽到卡片")
                .setConfirmText("OK")
                .showCancelButton(false)
                .setConfirmClickListener {
                    EventBus.getDefault().post(EventCard(-1, consume))
                    it.cancel()
                    dismiss()
                }
                .show()
        } else {
            val sDiloag = SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("恭喜获得卡片")
                .setContentText("Level $res")
                .setConfirmText("OK")
                .showCancelButton(false)
                .setConfirmClickListener {
                    EventBus.getDefault().post(EventCard(res, consume))
                    it.cancel()
                    dismiss()
                }
            sDiloag.setCancelable(false)
            sDiloag.setCanceledOnTouchOutside(false)
            sDiloag.show()
        }



    }

}