package com.csh.studycollection

import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import cn.pedant.SweetAlert.SweetAlertDialog
import com.csh.studycollection.adapter.PlotAdapter
import com.csh.studycollection.base.BaseAppCompatActivity
import com.csh.studycollection.entity.Award
import com.csh.studycollection.entity.Coupon
import com.csh.studycollection.entity.Plot
import com.csh.studycollection.service.UserService
import kotlinx.android.synthetic.main.activity_card.*

class PlotActivity: BaseAppCompatActivity() {

    private var mAdapter: PlotAdapter? = null

    override fun provideLayoutRes() = R.layout.activity_plot

    override fun onAfterSetContentView() {
        back?.setOnClickListener { finish() }
        swr?.setOnRefreshListener { refreshRlv() }
        mAdapter = PlotAdapter(this, R.layout.item_plot, null)
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            clickItem(mAdapter?.data?.get(position)?:return@setOnItemChildClickListener)
        }
        rlv?.adapter = mAdapter
        queryPlots()
    }

    private fun clickItem(plot: Award) {
        if (plot.status == 0) {
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("确定要解锁吗")
                .setContentText("需消费相应卡片")
                .setCancelText("No")
                .setConfirmText("Yes")
                .showCancelButton(true)
                .setConfirmClickListener {
                    it.cancel()
                    lockNextPartPre(plot)
                }
                .setCancelClickListener { sDialog -> sDialog.cancel() }
                .show()
        } else if (plot.status == 1) {
            toasty("此功能开发中...")
        }
    }

    private fun refreshRlv() {
        queryPlots()
    }

    val pDialog by lazy { SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE) }
    private fun lockNextPartPre(plot: Award) {
        pDialog.show()
        val user = UserService.getCurrentLoginUser()

        val query1 = BmobQuery<Coupon>()
            .addWhereEqualTo("userId", user?.objectId)
        val query2 = BmobQuery<Coupon>()
            .addWhereEqualTo("status", 0) // 获取未使用过的卡片
        val andQuerys = ArrayList<BmobQuery<Coupon>>()
        andQuerys.add(query1)
        andQuerys.add(query2)

        BmobQuery<Coupon>()
            .and(andQuerys)
            .include("card")
            .findObjects(object: FindListener<Coupon>(){
                override fun done(cl: MutableList<Coupon>?, e: BmobException?) {
                    swr?.isRefreshing = false
                    if (e == null) {
                        if (!cl.isNullOrEmpty()) {
                            if (cl.any { it.card?.level!! >= plot.plot?.cardNeed!! }) {
                                // 消耗相应的卡片解锁剧集(status = 1)
                                val min = cl.filter { it.card?.level!! >= plot.plot?.cardNeed!! }.sortedBy { it.card?.level }[0]
                                min.status = 1
                                min.update(min.objectId, object: UpdateListener(){
                                    override fun done(e: BmobException?) {
                                        if (e == null) {
                                            lockNextPart(plot)
                                        } else {
                                            pDialog.dismiss()
                                            toasty("解锁失败~")
                                        }
                                    }
                                })
                            } else {
                                pDialog.dismiss()
                                toasty("没有相应卡片可解锁~")
                            }
                        } else {
                            pDialog.dismiss()
                            toasty("没有卡片可解锁~")
                        }
                    } else {
                        pDialog.dismiss()
                        toasty("获取卡片数据失败")
                    }
                }
            })
    }

    private fun lockNextPart(plot: Award) {
        // 1.解锁下一章
        plot.status = 1
        plot.update(plot.objectId, object: UpdateListener(){
            override fun done(e: BmobException?) {
                if (e == null) {
                    toasty("解锁成功~")
                    mAdapter?.notifyDataSetChanged()
                    // 2.获取下一章
                    BmobQuery<Plot>()
                        .addWhereEqualTo("objectId", plot.plot?.next)
                        .findObjects(object: FindListener<Plot>(){
                            override fun done(pl: MutableList<Plot>?, e: BmobException?) {
                                if (e == null) {
                                    if (!pl.isNullOrEmpty()) {
                                        val p = pl[0]
                                        val a = Award(UserService.getCurrentLoginUser()?.objectId, p, 0)
                                        a.save(object: SaveListener<String>(){
                                            override fun done(objectId: String?, e: BmobException?) {
                                                pDialog.dismiss()
                                                if (e == null) {
                                                    queryPlots()
                                                } else {}
                                            }
                                        })
                                    } else {
                                        pDialog.dismiss()
                                    }
                                } else {
                                    pDialog.dismiss()
                                }
                            }
                        })
                } else {
                    pDialog.dismiss()
                    toasty("解锁失败~")
                }
            }
        })
    }

    private fun queryPlots() {
        val user = UserService.getCurrentLoginUser()
        BmobQuery<Award>()
            .addWhereEqualTo("userId", user?.objectId?:return)
            .include("plot")
            .findObjects(object: FindListener<Award>(){
                override fun done(list: MutableList<Award>?, e: BmobException?) {
                    swr?.isRefreshing = false
                    if (e == null) {
                        if (list?.isNullOrEmpty() == true) {
                            queryDefaultAndSave()
                            return
                        }
                        mAdapter?.data?.clear()
                        mAdapter?.data?.addAll(list)
                        mAdapter?.notifyDataSetChanged()
                    } else {
                        toasty("获取数据失败：${e.message}")
                    }
                }
            })
    }

    private fun queryDefaultAndSave() {
        BmobQuery<Plot>()
            .addWhereEqualTo("objectId", "oP9MUUUm")
            .findObjects(object: FindListener<Plot>(){
                override fun done(list: MutableList<Plot>?, e: BmobException?) {
                    if (e == null) {
                        if (list?.isNullOrEmpty() == true) {
                            toasty("没有数据~")
                            return
                        }
                        // 添加
                        val a = Award(UserService.getCurrentLoginUser()?.objectId, list[0], 0)
                        a.save(object: SaveListener<String>(){
                            override fun done(objectId: String?, e: BmobException?) {
                                if (e == null) {
                                    queryPlots()
                                } else {
                                    toasty("没有数据~")
                                }
                            }
                        })
                    } else {
                        toasty("获取数据失败：${e.message}")
                    }
                }
            })
    }

}