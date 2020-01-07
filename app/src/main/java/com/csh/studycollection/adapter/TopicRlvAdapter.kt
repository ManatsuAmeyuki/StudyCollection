package com.csh.studycollection.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.csh.studycollection.R
import com.csh.studycollection.entity.Exercise
import com.csh.studycollection.utils.DensityUtil
import com.csh.studycollection.view.XTextView
import com.orhanobut.logger.Logger
import java.io.File

class TopicRlvAdapter(
    val cxt: Context,
    val d: MutableList<Exercise>? = mutableListOf(),
    val listener: OnAnswerQuestionListener? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int) =
        d?.get(position)?.topic?.type ?: super.getItemViewType(position)

    override fun getItemCount() = d?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            1 -> {
                val immediateView = LayoutInflater.from(cxt).inflate(R.layout.fragment_do_question_immediate, parent, false)
                return ImmediateViewHolder(
                    immediateView
                )
            }
            4 -> {
                val explanationView =
                    LayoutInflater.from(cxt).inflate(R.layout.fragment_do_question_explanation, parent, false)
                return ExplanationViewHolder(
                    explanationView
                )
            }
            else -> {
                val explanationView =
                    LayoutInflater.from(cxt).inflate(R.layout.fragment_do_question_explanation, parent, false)
                return ExplanationViewHolder(
                    explanationView
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            1 -> {
                val e = d?.get(position)
                val mHolder = holder as ImmediateViewHolder
                mHolder.mType.text = "选择题"
                mHolder.mQuestion.text = "${position+1}、${d?.get(position)?.topic?.question}"
                mHolder.mOptsContanier.removeAllViews()

                var opts: List<String>?
                if (d?.get(position)?.topic?.options?.contains("#") == true) {
                    opts = d.get(position).topic?.options?.split("#")
                }
                opts = d?.get(position)?.topic?.options?.split("\n")
                if (opts == null) return
                for ((index, t) in opts.withIndex()) {
                    val tv = XTextView(cxt)
                    val lp = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    lp.topMargin = DensityUtil.dip2px(cxt, 20f)
                    tv.layoutParams = lp
                    tv.textSize = 20f
                    tv.setTextColor(cxt.resources.getColor(R.color.white))
                    tv.text = t
                    tv.compoundDrawablePadding = DensityUtil.dip2px(cxt, 5f)
                    if (e?.answer != null && e.answer?.trim()?.first() == t.trim().first()) {
//                        e.answer?.trim()?.first() == e.topic?.solution?.trim()?.first() // 判断正确与否
                        tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_choose_checked, 0, 0, 0)
                    } else {
                        tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_choose_normal, 0, 0, 0)
                    }
                    tv.setOnClickListener {
                        d?.get(position)?.answer = t
                        notifyItemChanged(position)
                        listener?.onAnswered(position)
                    }
                    mHolder.mOptsContanier.addView(tv)
                }
            }
            4 -> {
                val mHolder = holder as ExplanationViewHolder
                mHolder.mType.text = "解答题"
                mHolder.mQuestion.text = "${position+1}、${d?.get(position)?.topic?.question}"
                if (d?.get(position)?.localUrl != null) {
                    Glide
                        .with(cxt)
                        .applyDefaultRequestOptions(
                            RequestOptions()
                                .placeholder(R.drawable.ic_camera_btn).diskCacheStrategy(
                                    DiskCacheStrategy.NONE
                                )
                        )
                        .load(File(d[position].localUrl))
                        .into(mHolder.cameraBtn)
                } else if (d?.get(position)?.answerFileUrl != null ) {
                    Glide
                        .with(cxt)
                        .applyDefaultRequestOptions(
                            RequestOptions()
                                .placeholder(R.drawable.ic_camera_btn).diskCacheStrategy(
                                    DiskCacheStrategy.NONE
                                )
                        )
                        .load(d[position].answerFileUrl)
                        .into(mHolder.cameraBtn)
                }
                mHolder.cameraBtn.setOnClickListener {
                    listener?.onTakePhoto(position)
                }
            }
        }
    }

    class ImmediateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mType = itemView.findViewById<TextView>(R.id.topicTypeTv)
        var mQuestion = itemView.findViewById<TextView>(R.id.questionTv)
        var mOptsContanier = itemView.findViewById<LinearLayout>(R.id.optionsContainer)
    }

    class ExplanationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mType = itemView.findViewById<TextView>(R.id.topicTypeTv)
        var mQuestion = itemView.findViewById<TextView>(R.id.questionTv)
        var cameraBtn = itemView.findViewById<ImageView>(R.id.cameraBtn)
    }

    interface OnAnswerQuestionListener {
        fun onAnswered(position: Int)
        fun onTakePhoto(position: Int)
    }
}