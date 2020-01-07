package com.csh.studycollection

import android.content.Intent
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.csh.studycollection.adapter.CardAdapter
import com.csh.studycollection.base.BaseAppCompatActivity
import com.csh.studycollection.entity.Coupon
import com.csh.studycollection.service.UserService
import kotlinx.android.synthetic.main.activity_card.*


class CardActivity: BaseAppCompatActivity() {

    private var mAdapter: CardAdapter? = null

    override fun provideLayoutRes() = R.layout.activity_card

    override fun onAfterSetContentView() {
        back?.setOnClickListener { finish() }
        tvPlot?.setOnClickListener{ startActivity(Intent(this, PlotActivity::class.java)) }
        swr?.setOnRefreshListener { refreshRlv() }
        mAdapter = CardAdapter(this, R.layout.item_card, null)
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            val item = mAdapter?.data?.get(position)

        }
        rlv?.adapter = mAdapter
        queryUserCards()
    }

    private fun refreshRlv() {
        queryUserCards()
    }

    private fun queryUserCards() {
        val user = UserService.getCurrentLoginUser()
        BmobQuery<Coupon>()
            .addWhereEqualTo("userId", user?.objectId)
            .include("card")
            .findObjects(object: FindListener<Coupon>(){
                override fun done(cl: MutableList<Coupon>?, e: BmobException?) {
                    swr?.isRefreshing = false
                    if (e == null) {
                        if (!cl.isNullOrEmpty()) {
                            mAdapter?.data?.clear()
                            mAdapter?.data?.addAll(cl)
                            mAdapter?.notifyDataSetChanged()
                        } else {
                            toasty("没有数据~")
                        }
                    } else {
                        toasty("获取数据失败")
                    }
                }
            })
    }

}