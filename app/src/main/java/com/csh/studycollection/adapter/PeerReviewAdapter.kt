package com.csh.studycollection.adapter

import android.content.Context
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.csh.studycollection.R
import com.csh.studycollection.entity.Exercise
import com.csh.studycollection.utils.Configs

class PeerReviewAdapter(val cxt: Context, layoutResId: Int, data: MutableList<Exercise>?) :
    BaseQuickAdapter<Exercise, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder?, item: Exercise?) {
        helper?.setText(R.id.tvQuestion, item?.topic?.question)
            ?.addOnClickListener(R.id.iv)
            ?.addOnClickListener(R.id.tvComment)
        Glide.with(cxt).load("${Configs.domain}${item?.answerFileUrl}").into(helper?.getView(R.id.iv)?:return)
    }

}