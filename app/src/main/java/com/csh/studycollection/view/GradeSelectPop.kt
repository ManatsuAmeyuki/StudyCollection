package com.csh.studycollection.view

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.UpdateListener
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.csh.studycollection.R
import com.csh.studycollection.entity.EventUpdateSettingDisplay
import com.csh.studycollection.entity.Grade
import com.csh.studycollection.entity.User
import com.hjq.toast.ToastUtils
import com.lxj.xpopup.impl.FullScreenPopupView
import org.greenrobot.eventbus.EventBus

class GradeSelectPop(private val cxt: Context) : FullScreenPopupView(cxt) {

    private var mRlv: RecyclerView? = null
    private var mAdapter: MyAdapter? = null

    override fun getImplLayoutId(): Int {
        return R.layout.pop_grade_select
    }

    override fun onCreate() {
        super.onCreate()
        findViewById<TextView>(R.id.title).text = "请选择年级"
        findViewById<View>(R.id.xClose).setOnClickListener { this.dismiss() }
        mRlv = findViewById(R.id.mRlv)

        mRlv?.layoutManager = GridLayoutManager(cxt, 4)
        mRlv?.addItemDecoration(GridImageDecoration(cxt, 4, 0f, 20f))
        mAdapter = MyAdapter(R.layout.item_grid_pop, null)
        mRlv?.adapter = mAdapter
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            updateUserGradeInfo(mAdapter?.data?.get(position)?:return@setOnItemChildClickListener)
        }
        getGradeList()
    }

    private fun getGradeList() {
        BmobQuery<Grade>().findObjects(object: FindListener<Grade>(){
            override fun done(gs: MutableList<Grade>?, e: BmobException?) {
                mRlv?.postDelayed({
                    if (e == null) {
                        mAdapter?.data?.clear()
                        mAdapter?.data?.addAll(gs?: mutableListOf())
                        mAdapter?.notifyDataSetChanged()
                    } else {
                        ToastUtils.show("获取年级信息失败~")
                        this@GradeSelectPop.dismiss()
                    }
                }, 500)
            }
        })
    }

    private fun updateUserGradeInfo(grade: Grade) {
        val user = BmobUser.getCurrentUser(User::class.java)
        user.grade = grade
        user.update(object : UpdateListener() {
            override fun done(e: BmobException?) {
                if (e == null) {
                    ToastUtils.show("设置成功~")
                    EventBus.getDefault().post(EventUpdateSettingDisplay())
                    this@GradeSelectPop.dismiss()
                } else {
                    ToastUtils.show("设置失败~")
                }
            }
        })
    }

    class MyAdapter(layoutResId: Int, data: MutableList<Grade>?) :
        BaseQuickAdapter<Grade, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder?, item: Grade?) {
            helper?.setText(R.id.itemTv, item?.name)
                ?.addOnClickListener(R.id.itemTv)
        }

    }
}