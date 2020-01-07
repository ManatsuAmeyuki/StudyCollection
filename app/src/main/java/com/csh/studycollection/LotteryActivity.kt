package com.csh.studycollection

import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.datatype.BmobRelation
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import cn.pedant.SweetAlert.SweetAlertDialog
import com.csh.studycollection.base.BaseAppCompatActivity
import com.csh.studycollection.entity.Card
import com.csh.studycollection.entity.Coupon
import com.csh.studycollection.entity.EventCard
import com.csh.studycollection.entity.User
import com.csh.studycollection.service.UserService
import com.csh.studycollection.view.LotteryAnimationPop
import com.lxj.xpopup.XPopup
import kotlinx.android.synthetic.main.activity_lottery.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class LotteryActivity: BaseAppCompatActivity() {

    companion object {
        const val CONSUME_10 = 10
        const val CONSUME_20 = 20
    }

    override fun provideLayoutRes() = R.layout.activity_lottery

    override fun onAfterSetContentView() {
        back?.setOnClickListener { finish() }

        lottery1?.setOnClickListener {
            val user = BmobUser.getCurrentUser(User::class.java)
            if (user.integral == null || user.integral!! < 10) {
                toasty("积分不足~")
                return@setOnClickListener
            }
            doLottery(CONSUME_10)
        }
        lottery10?.setOnClickListener {
            val user = BmobUser.getCurrentUser(User::class.java)
            if (user.integral == null || user.integral!! < 20) {
                toasty("积分不足~")
                return@setOnClickListener
            }
            doLottery(CONSUME_20)
        }
        //
        queryUserScore()
    }

    private fun doLottery(consume: Int) {
        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("消费积分赢得卡片才能解锁新剧集哦~")
            .setContentText("")
            .setCancelText("No")
            .setConfirmText("Yes")
            .showCancelButton(true)
            .setConfirmClickListener {
                it.cancel()
                XPopup.Builder(this)
                    .dismissOnBackPressed(false)
                    .dismissOnTouchOutside(false)
                    .asCustom(LotteryAnimationPop(this, consume))
                    .show()
            }
            .setCancelClickListener { sDialog -> sDialog.cancel() }
            .show()
    }


    private fun queryUserScore() {
        val user = BmobUser.getCurrentUser(User::class.java)
        total.text= "+ ${user.integral?:0}"
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: EventCard) {
        updateUserInfo(event.consume, event.cardLevel)
    }

    /**
     *
     * @param s 消费了多少积分
     * @param c 获得的卡片等级
     */
    private fun updateUserInfo(s: Int, c: Int) {
        val user = BmobUser.getCurrentUser(User::class.java)
        user.integral = (user.integral?:0)-s
        user.update(object : UpdateListener() {
            override fun done(e: BmobException?) {
                if (e == null) {
                    queryUserScore()
                } else {
                    toasty("更新积分失败")
                }
            }
        })
        // 未抽到任何卡片
        if (c == -1) return
        // 获取卡片
        BmobQuery<Card>()
            .addWhereEqualTo("level", c)
            .findObjects(object: FindListener<Card>(){
                override fun done(cards: MutableList<Card>?, e: BmobException?) {
                    if (e == null) {
                        val card = cards?.get(0)?:return
                        val coupon = Coupon(UserService.getCurrentLoginUser()?.objectId, card)
                        coupon.save(object: SaveListener<String>(){
                            override fun done(objectId: String?, e: BmobException?) {
                                if (e == null) {
                                    toasty("获得卡片成功~")
                                    queryUserScore()
                                } else {
                                    toasty("添加卡片失败")
                                }
                            }
                        })
                    } else {
                        toasty("获取卡片信息失败")
                    }
                }
            })
    }

}