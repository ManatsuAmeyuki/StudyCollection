package com.csh.studycollection

import android.content.Intent
import android.view.View
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.datatype.BmobQueryResult
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SQLQueryListener
import cn.pedant.SweetAlert.SweetAlertDialog
import com.csh.studycollection.base.BaseAppCompatActivity
import com.csh.studycollection.entity.EventFilterTopics
import com.csh.studycollection.entity.Subject
import com.csh.studycollection.entity.Topic
import com.csh.studycollection.service.UserService
import com.csh.studycollection.view.SubjectSelectPop
import com.lxj.xpopup.XPopup
import kotlinx.android.synthetic.main.activity_home.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList


class HomeActivity: BaseAppCompatActivity() {

    override fun provideLayoutRes() = R.layout.activity_home

    override fun onAfterSetContentView() {
        studyBtn?.setOnClickListener {
            XPopup.Builder(this).asCustom(SubjectSelectPop(this)).show()
        }
        integralBtn?.setOnClickListener {
            startActivity(Intent(this, LotteryActivity::class.java))
        }

        ivSetting?.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
    }

    override fun onBackPressed() {
        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("是否退出?")
            .setContentText(" ")
            .setCancelText("No")
            .setConfirmText("Yes")
            .showCancelButton(true)
            .setConfirmClickListener {
                UserService.logout()
                finish()
            }
            .setCancelClickListener { sDialog -> sDialog.cancel() }
            .show()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: EventFilterTopics) {
        if (event.sb != null) {
            optTopics(event.sb)
            circleProgressView.progress = 10
            //circleProgressView.setTurn(true)
        }
    }

    /**
     * 暂时仅根据学科筛选（年级、地区先忽略），默认5道选择题，一道大题
     */
    private fun optTopics(subject: Subject, types: MutableList<Int>? = null, num: Int = 5) {
        loading.visibility = View.VISIBLE
        val topics = ArrayList<Topic>()

        // 5道选择题
//        val bql1 = "select * from Topic where subjectCode='${subject.code}' and type=1 order by rand() limit 10"
        val bql1 = "select * from Topic where subjectCode='${subject.code}' and type=1"
        BmobQuery<Topic>().doSQLQuery(bql1, object: SQLQueryListener<Topic>(){
            override fun done(p0: BmobQueryResult<Topic>?, e: BmobException?) {
                if (e == null) {
                    if (p0?.results == null || p0.results?.size!! <= 0) {
                        toasty("题库数量不足~")
                        loading.visibility = View.GONE
                        return
                    }
                    topics.addAll(randomChooseTopics(p0.results?:return, num))

                    // 1道大题
                    val bql2 = "select * from Topic where subjectCode='${subject.code}' and type=4"
                    BmobQuery<Topic>().doSQLQuery(bql2, object: SQLQueryListener<Topic>(){
                        override fun done(p0: BmobQueryResult<Topic>?, e: BmobException?) {
                            if (e == null) {
                                topics.addAll(randomChooseTopics(p0?.results?:return, 1))
                                circleProgressView.showAnimation(100, 1000)
                                circleProgressView.setOnChangeListener { progress, max ->
                                    if (progress >= max) {
                                        val int = Intent(this@HomeActivity, AnswerActivity::class.java)
                                        int.putExtra(AnswerActivity.EXTRA_TOPICS, topics)
                                        startActivity(int)
                                        loading.postDelayed({
                                            loading.visibility = View.GONE
                                        }, 500)
                                    }
                                }

                            } else {
                                toasty("选题失败~")
                                loading.visibility = View.GONE
                            }
                        }
                    })
                } else {
                    toasty("选题失败~")
                    loading.visibility = View.GONE
                }
            }
        })

    }

    private fun randomChooseTopics(topics: MutableList<Topic>, count: Int): MutableList<Topic> {
        val index = Random()
        val indexList = ArrayList<Int>()
        val newList = ArrayList<Topic>()
        if (topics.isEmpty()) return newList
        var i = 0
        while (i < count) {
            // 返回内的随机数
            val j = index.nextInt(topics.size)
            // 判断是否重复
            if (indexList.contains(j)) {
                continue
            } else {
                // 获取元素
                indexList.add(j)
                newList.add(topics.get(j))
            }
            i++
        }
        return newList
    }

}