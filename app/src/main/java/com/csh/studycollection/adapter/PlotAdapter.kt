package com.csh.studycollection.adapter

import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.csh.studycollection.R
import com.csh.studycollection.entity.Award

class PlotAdapter(val cxt: Context, layoutResId: Int, data: MutableList<Award>?) :
    BaseQuickAdapter<Award, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder?, item: Award?) {
        helper?.setBackgroundRes(R.id.rlRoot, if (item?.status == 0) R.drawable.bg_lock else R.drawable.bg_unlock)
            ?.setText(R.id.tvName, item?.plot?.title)
            ?.addOnClickListener(R.id.rlRoot)
    }

}