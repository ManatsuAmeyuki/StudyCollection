package com.csh.studycollection.view

import android.content.Context
import android.os.Build
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.csh.studycollection.R
import com.csh.studycollection.entity.Exercise
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.impl.FullScreenPopupView

class AnswerSheetPop(
    private val cxt: Context,
    private val d: MutableList<Exercise>? = null,
    private val listener: OnConfirmSubmitListener? = null
) : BottomPopupView(cxt) {

    private var mRlv: RecyclerView? = null
    private var mAdapter: MyAdapter? = null

    override fun getImplLayoutId(): Int {
        return R.layout.pop_answer_sheet
    }

    override fun onCreate() {
        super.onCreate()
        findViewById<TextView>(R.id.title).text = "答题卡"
        findViewById<View>(R.id.submitAnswerSheet).setOnClickListener { submitAnswerSheet() }
        mRlv = findViewById(R.id.mRlv)

        mAdapter = MyAdapter(R.layout.item_answer_sheet, d)
        mRlv?.adapter = mAdapter

    }

    private fun submitAnswerSheet() {
        SweetAlertDialog(cxt, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("确定要提交答案吗?")
            .setContentText(" ")
            .setCancelText("No")
            .setConfirmText("Yes")
            .showCancelButton(true)
            .setConfirmClickListener {
                listener?.confirmSubmit()
                it.cancel()
            }
            .setCancelClickListener { sDialog -> sDialog.cancel() }
            .show()
    }

    class MyAdapter(layoutResId: Int, data: MutableList<Exercise>?) :
        BaseQuickAdapter<Exercise, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder?, item: Exercise?) {
            if (item?.topic?.type == 1) {
                helper?.getView<TextView>(R.id.tvAnswer)?.text =
                    "我的答案：${item.answer ?: "未作答"}"
            } else if (item?.topic?.type == 4) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    helper?.getView<TextView>(R.id.tvAnswer)?.text =
                        Html.fromHtml("<font color='#87C2FF'>解答题暂不记录积分</font>", Html.FROM_HTML_MODE_COMPACT)
                } else {
                    helper?.getView<TextView>(R.id.tvAnswer)?.text =
                        Html.fromHtml("<font color='#87C2FF'>解答题暂不记录积分</font>")
                }
            }
            helper?.setText(R.id.tvQuestion, "Q：${item?.topic?.question}")
        }
    }

    interface OnConfirmSubmitListener {
        fun confirmSubmit()
    }
}