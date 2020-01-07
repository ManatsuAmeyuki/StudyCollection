package com.csh.studycollection.adapter

import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.csh.studycollection.R
import com.csh.studycollection.entity.Card
import com.csh.studycollection.entity.Coupon

class CardAdapter(val cxt: Context, layoutResId: Int, data: MutableList<Coupon>?) :
    BaseQuickAdapter<Coupon, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder?, c: Coupon?) {
        val s = if (c?.status == 0) "未使用" else "已使用"
        val item = c?.card
        helper?.setText(R.id.tvName, "${item?.name}（$s）")
            ?.setText(R.id.tvValue, "Level ${item?.level}")
            ?.setText(R.id.tvDesc, "${item?.desc}")
    }

}