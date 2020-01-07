package com.csh.studycollection

import android.content.Intent
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.pedant.SweetAlert.SweetAlertDialog
import com.csh.studycollection.base.BaseAppCompatActivity
import com.csh.studycollection.entity.*
import com.csh.studycollection.service.UserService
import com.csh.studycollection.view.AreaSelectPop
import com.csh.studycollection.view.GradeSelectPop
import com.lxj.xpopup.XPopup
import kotlinx.android.synthetic.main.activity_setting.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SettingActivity: BaseAppCompatActivity() {

    override fun provideLayoutRes() = R.layout.activity_setting

    override fun onAfterSetContentView() {
        back?.setOnClickListener { finish() }
        exit?.setOnClickListener { logout() }

        llGrade?.setOnClickListener { showGradeWindow() }
        llArea?.setOnClickListener { showAreaWindow() }
        llPeerReview?.setOnClickListener { startActivity(Intent(this, PeerReviewActivity::class.java)) }
        llCard?.setOnClickListener { startActivity(Intent(this, CardActivity::class.java)) }
    }

    private fun showGradeWindow() {
        XPopup.Builder(this).asCustom(GradeSelectPop(this)).show();
    }

    private fun showAreaWindow() {
        XPopup.Builder(this).asCustom(AreaSelectPop(this)).show()
    }

    override fun onResume() {
        super.onResume()
        updateDisplay()
    }

    private fun logout() {
        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("是否退出?")
            .setContentText(" ")
            .setCancelText("No")
            .setConfirmText("Yes")
            .showCancelButton(true)
            .setConfirmClickListener {
                UserService.logout()
                EventBus.getDefault().post(EventFinishAct())
            }
            .setCancelClickListener { sDialog -> sDialog.cancel() }
            .show()
    }

    private fun updateDisplay () {
        val user = BmobUser.getCurrentUser(User::class.java)
        BmobQuery<Area>()
            .addWhereEqualTo("objectId", user.area?.objectId)
            .findObjects(object: FindListener<Area>(){
                override fun done(areas: MutableList<Area>?, e: BmobException?) {
                    if (e == null && areas?.isNullOrEmpty() == false) {
                        tvArea?.text = areas[0].name
                    }
                }
            })
        BmobQuery<Grade>()
            .addWhereEqualTo("objectId", user.grade?.objectId)
            .findObjects(object: FindListener<Grade>(){
                override fun done(gs: MutableList<Grade>?, e: BmobException?) {
                    if (e == null && gs?.isNullOrEmpty() == false) {
                        tvGrade?.text = "， ${gs[0].name}"
                    }
                }
            })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: EventUpdateSettingDisplay) {
        updateDisplay ()
    }

}