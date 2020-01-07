package com.csh.studycollection.view

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.csh.studycollection.R
import com.csh.studycollection.entity.EventFilterTopics
import com.csh.studycollection.entity.Subject
import com.hjq.toast.ToastUtils
import com.lxj.xpopup.impl.FullScreenPopupView
import org.greenrobot.eventbus.EventBus

class SubjectSelectPop(private val cxt: Context) : FullScreenPopupView(cxt) {

    private var mRlv: RecyclerView? = null
    private var mAdapter: MyAdapter? = null

    override fun getImplLayoutId(): Int {
        return R.layout.pop_grade_select
    }

    override fun onCreate() {
        super.onCreate()
        findViewById<TextView>(R.id.title).text = "请选择科目"
        findViewById<View>(R.id.xClose).setOnClickListener { this.dismiss() }
        mRlv = findViewById(R.id.mRlv)

        mRlv?.layoutManager = GridLayoutManager(cxt, 4)
        mRlv?.addItemDecoration(GridImageDecoration(cxt, 4, 5f, 5f))
        mAdapter = MyAdapter(R.layout.item_grid_pop, null)
        mRlv?.adapter = mAdapter
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            EventBus.getDefault().post(EventFilterTopics(mAdapter?.data?.get(position)))
            this.dismiss()
        }
        getSubjectList()
    }

    private fun getSubjectList() {
        BmobQuery<Subject>().findObjects(object: FindListener<Subject>(){
            override fun done(sbs: MutableList<Subject>?, e: BmobException?) {
                mRlv?.postDelayed({
                    if (e == null) {
                        mAdapter?.data?.clear()
                        mAdapter?.data?.addAll(sbs?: mutableListOf())
                        mAdapter?.notifyDataSetChanged()
                    } else {
                        ToastUtils.show("获取科目信息失败~")
                        this@SubjectSelectPop.dismiss()
                    }
                }, 500)
            }
        })
    }

    class MyAdapter(layoutResId: Int, data: MutableList<Subject>?) :
        BaseQuickAdapter<Subject, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder?, item: Subject?) {
            helper?.setText(R.id.itemTv, item?.name)
                ?.addOnClickListener(R.id.itemTv)
        }

    }
}