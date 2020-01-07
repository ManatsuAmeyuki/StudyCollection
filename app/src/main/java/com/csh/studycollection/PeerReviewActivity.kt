package com.csh.studycollection

import android.content.Context
import android.widget.ImageView
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.csh.studycollection.adapter.PeerReviewAdapter
import com.csh.studycollection.base.BaseAppCompatActivity
import com.csh.studycollection.entity.Exercise
import com.csh.studycollection.utils.Configs
import com.csh.studycollection.view.CommentPanelPop
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.XPopupImageLoader
import kotlinx.android.synthetic.main.activity_peer_review.*
import java.io.File


class PeerReviewActivity: BaseAppCompatActivity(), BaseQuickAdapter.RequestLoadMoreListener {

    private var mAdapter: PeerReviewAdapter? = null
    private var mCurrentPageNum = 0

    override fun provideLayoutRes() = R.layout.activity_peer_review

    override fun onAfterSetContentView() {
        back?.setOnClickListener { finish() }
        swr?.setOnRefreshListener { refreshRlv() }
        mAdapter = PeerReviewAdapter(this, R.layout.item_peer_review, null)
//        mAdapter?.setEnableLoadMore(true)
//        mAdapter?.setOnLoadMoreListener(this, rlv)
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            val item = mAdapter?.data?.get(position)
            if (view.id == R.id.iv) {
                popImageView(view as ImageView, "${Configs.domain}${item?.answerFileUrl}")
            }
            if (view.id == R.id.tvComment) {
                XPopup
                    .Builder(this)
                    .asCustom(CommentPanelPop(this, item?:return@setOnItemChildClickListener))
                    .show()
            }
        }
        rlv?.adapter = mAdapter
        queryExercises()
    }

    private fun refreshRlv() {
        mCurrentPageNum = 0
        queryExercises()
    }

    override fun onLoadMoreRequested() {
        mCurrentPageNum++
        queryExercises()
    }

    private fun queryExercises() {
        val query1 = BmobQuery<Exercise>()
        query1.addWhereNotEqualTo("answerFileUrl", "")
        val query2 = BmobQuery<Exercise>()
        query1.addWhereExists("answerFileUrl")

        val andQuerys = ArrayList<BmobQuery<Exercise>>()
        andQuerys.add(query1)
        andQuerys.add(query2)

//        BmobQuery<Exercise>()
//            .addWhereExists("answerFileUrl")
//            .include("user,topic") // 希望同时查询该习题的用户和题目信息
        BmobQuery<Exercise>()
            .and(andQuerys)
            .include("user,topic") // 希望同时查询该习题的用户和题目信息
            .setLimit(Configs.pageSize)
            .setSkip(mCurrentPageNum * Configs.pageSize)
            .findObjects(object: FindListener<Exercise>(){
                override fun done(list: MutableList<Exercise>?, e: BmobException?) {
                    swr?.isRefreshing = false
                    if (e == null) {
                        if (list?.isNullOrEmpty() == true) {
                            mAdapter?.loadMoreEnd()
                            return
                        }
                        mAdapter?.loadMoreComplete()
                        if (mCurrentPageNum == 0) {
                            mAdapter?.data?.clear()
                        }
                        mAdapter?.data?.addAll(list?:return)
                        mAdapter?.notifyDataSetChanged()
                    } else {
                        mAdapter?.loadMoreFail()
                        toasty("获取数据失败：${e.message}")
                    }
                }
            })

//        val bql = "select * from Exercise where answerFileUrl is  not  null and answerFileUrl!=''"
//        val query = BmobQuery<Exercise>()
//        query.setSQL(bql)
//        query.doSQLQuery(object: SQLQueryListener<Exercise>(){
//            override fun done(result: BmobQueryResult<Exercise>?, e: BmobException?) {
//                swr?.isRefreshing = false
//                val list = result?.results
//                if (e == null) {
//                    if (list?.isNullOrEmpty() == true) {
//                        toasty("没有数据~")
//                        return
//                    }
//                    mAdapter?.data?.clear()
//                    mAdapter?.data?.addAll(list ?: return)
//                    mAdapter?.notifyDataSetChanged()
//                } else {
//                    toasty("获取数据失败：${e.message}")
//                }
//            }
//        })

    }


    /**
     * 大图展示
     */
    private fun popImageView(iv: ImageView, url: String) {
        XPopup.Builder(this)
            .asImageViewer(
                iv,
                url,
                false,
                -1,
                -1,
                -1,
                false,
                ImageLoader()
            )
            .show()
    }
    // 图片加载器，这里以Glide为例
    class ImageLoader : XPopupImageLoader {
        override fun loadImage(position: Int, uri: Any, imageView: ImageView) {
            Glide.with(imageView).load(uri).into(imageView)
        }
        //必须实现这个方法，返回uri对应的缓存文件，可参照下面的实现，内部保存图片会用到。如果你不需要保存图片这个功能，可以返回null。
        override fun getImageFile(context: Context, uri: Any): File? {
            try {
                return null
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }
}
