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
import com.csh.studycollection.entity.Area
import com.csh.studycollection.entity.EventUpdateSettingDisplay
import com.csh.studycollection.entity.User
import com.hjq.toast.ToastUtils
import com.lxj.xpopup.impl.FullScreenPopupView
import org.greenrobot.eventbus.EventBus

class AreaSelectPop(private val cxt: Context) : FullScreenPopupView(cxt) {

    private var mRlv: RecyclerView? = null
    private var mAdapter: MyAdapter? = null

    override fun getImplLayoutId(): Int {
        return R.layout.pop_area_select
    }

    override fun onCreate() {
        super.onCreate()
        findViewById<TextView>(R.id.title).text = "请选择地区"
        findViewById<View>(R.id.xClose).setOnClickListener { this.dismiss() }
        mRlv = findViewById(R.id.mRlv)

        mRlv?.layoutManager = GridLayoutManager(cxt, 4)
        mRlv?.addItemDecoration(GridImageDecoration(cxt, 4, 5f, 5f))
        mAdapter = MyAdapter(R.layout.item_grid_pop, null)
        mRlv?.adapter = mAdapter
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            updateUserAreaInfo(mAdapter?.data?.get(position)?:return@setOnItemChildClickListener)
        }
        getAreaList()
    }

    private fun getAreaList() {
        BmobQuery<Area>().findObjects(object: FindListener<Area>(){
            override fun done(areas: MutableList<Area>?, e: BmobException?) {
                mRlv?.postDelayed({
                    if (e == null) {
                        mAdapter?.data?.clear()
                        mAdapter?.data?.addAll(areas?: mutableListOf())
                        mAdapter?.notifyDataSetChanged()
                    } else {
                        ToastUtils.show("获取地区信息失败~")
                        this@AreaSelectPop.dismiss()
                    }
                }, 500)
            }
        })
    }

    private fun updateUserAreaInfo(area: Area) {
        val user = BmobUser.getCurrentUser(User::class.java)
        user.area = area
        user.update(object : UpdateListener() {
            override fun done(e: BmobException?) {
                if (e == null) {
                    ToastUtils.show("设置成功~")
                    EventBus.getDefault().post(EventUpdateSettingDisplay())
                    this@AreaSelectPop.dismiss()
                } else {
                    ToastUtils.show("设置失败~")
                }
            }
        })
    }

    class MyAdapter(layoutResId: Int, data: MutableList<Area>?) :
        BaseQuickAdapter<Area, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder?, item: Area?) {
            helper?.setText(R.id.itemTv, item?.name)
                ?.addOnClickListener(R.id.itemTv)
        }

    }
}