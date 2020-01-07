package com.csh.studycollection

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import cn.pedant.SweetAlert.SweetAlertDialog
import com.csh.studycollection.adapter.TopicRlvAdapter
import com.csh.studycollection.base.BaseAppCompatActivity
import com.csh.studycollection.entity.EventUpdateSettingDisplay
import com.csh.studycollection.entity.Exercise
import com.csh.studycollection.entity.Topic
import com.csh.studycollection.entity.User
import com.csh.studycollection.service.UserService
import com.csh.studycollection.utils.Configs
import com.csh.studycollection.utils.FilePathUtils
import com.csh.studycollection.utils.PermissionUtils
import com.csh.studycollection.view.AnswerSheetPop
import com.csh.studycollection.view.OnViewPagerListener
import com.csh.studycollection.view.ViewPagerLayoutManager
import com.hjq.toast.ToastUtils
import com.lxj.xpopup.XPopup
import com.oceanus.cameralibrary.util.TakePhotoUtils
import com.qiniu.android.storage.Configuration
import com.qiniu.android.storage.UploadManager
import com.qiniu.util.Auth
import kotlinx.android.synthetic.main.activity_answer.*
import org.greenrobot.eventbus.EventBus
import java.io.File


class AnswerActivity: BaseAppCompatActivity(), TopicRlvAdapter.OnAnswerQuestionListener,
    OnViewPagerListener, AnswerSheetPop.OnConfirmSubmitListener {

    companion object {
        const val EXTRA_TOPICS = "EXTRA_TOPICS"
        const val REQUEST_CODE_CAMERA = 1001
    }

    val mExercises = ArrayList<Exercise>()
    var mAdapter: TopicRlvAdapter? = null
    var mCurrentPosition = 0

    override fun provideLayoutRes() = R.layout.activity_answer

    override fun onAfterSetContentView() {
        val s = intent?.extras?.getSerializable(EXTRA_TOPICS)?:return
        (s as ArrayList<Topic>).forEach {
            mExercises.add(Exercise(UserService.getCurrentLoginUser(), it))
        }
        val vlm = ViewPagerLayoutManager(
            this,
            RecyclerView.HORIZONTAL,
            false
        )
        vlm.setOnViewPagerListener(this)
        topicRlv?.layoutManager = vlm
        mAdapter = TopicRlvAdapter(this, mExercises, this)
        topicRlv?.adapter = mAdapter

        nextBtn?.setOnClickListener {
            MoveToNextPosition(vlm)
        }
        skipBtn?.setOnClickListener {
            MoveToNextPosition(vlm)
        }
        submit?.setOnClickListener {
            XPopup.Builder(this).asCustom(AnswerSheetPop(this, mExercises, this)).show()
        }

    }
    val smoothScroller by lazy {
        object : LinearSmoothScroller(this) {
            override fun getVerticalSnapPreference(): Int {
                return LinearSmoothScroller.SNAP_TO_START
            }
        }
    }
    private fun MoveToNextPosition(manager: RecyclerView.LayoutManager) {
        mCurrentPosition++
        smoothScroller.targetPosition = mCurrentPosition
        manager.startSmoothScroll(smoothScroller)
    }

    override fun onInitComplete() {}

    override fun onPageRelease(isNext: Boolean, position: Int) {}

    override fun onPageSelected(position: Int, isBottom: Boolean) {
        mCurrentPosition = position
        if (position >= mExercises.size-1) {
            submit?.visibility = View.VISIBLE
            llBtns?.visibility = View.GONE
        } else {
            submit?.visibility = View.GONE
            llBtns?.visibility = View.VISIBLE
        }
    }

    val pDialog by lazy {
        SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
    }
    override fun confirmSubmit() {
        pDialog.show()
        val es = mExercises.filter { (it.localUrl != null || it.answer != null) }
        if (!es.isNullOrEmpty()) {
            for (e in mExercises) {
                e.user = UserService.getCurrentLoginUser()
            }
            // 开始提交习题
            saveExercisesStep1()
        } else {
            pDialog.dismiss()
            toasty("还没作答哦~")
        }
    }

    // step.1
    private fun saveExercisesStep1() {
        val exercises1 = mExercises.filter { (it.user != null && it.answer != null) }
        if (exercises1.isNullOrEmpty()) {
            // 提交拍照的习题
            saveExercisesStep2()
            return
        }
        var j = 0
        for (it in exercises1) {
            it.save(object : SaveListener<String>() {
                override fun done(objectId: String?, e: BmobException?) {
                    j++
                    if (e == null) {
                    } else {
                    }
                    if (j == exercises1.size) {
                        // 提交拍照的习题
                        saveExercisesStep2()
                    }
                }
            })
        }
    }

    // step.2
    private fun saveExercisesStep2() {
        val fExercises = mExercises.filter { it.localUrl != null }
        val total = fExercises.size
        if (!fExercises.isNullOrEmpty()) {
            for (bf in fExercises) {
                uploadFile(total, bf, getQnToken(), File(bf.localUrl))
            }
        } else {
            scoreFinish()
        }
    }

    override fun onBackPressed() {
        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("是否退出作答?")
            .setContentText("退出将没有积分奖励哦")
            .setCancelText("No")
            .setConfirmText("Yes")
            .showCancelButton(true)
            .setConfirmClickListener { super.onBackPressed() }
            .setCancelClickListener { sDialog -> sDialog.cancel() }
            .show()
    }

    var mTmpPhotoFilePath: String? = null
    private fun openCamera() {
        PermissionUtils.requestPermissions(this,
            {
                mTmpPhotoFilePath =
                    "${FilePathUtils.getCameraPicDir(this)}${File.separator}${System.currentTimeMillis()}.jpg"
                TakePhotoUtils.openSystCamera(this, mTmpPhotoFilePath, AnswerActivity.REQUEST_CODE_CAMERA)
            },
            { PermissionUtils.gotoSettingPage(this) },
            { PermissionUtils.gotoSettingPage(this) },
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE)
    }

    var mCurrentTakePhotoPosition: Int? = null
    override fun onTakePhoto(position: Int) {
        mCurrentTakePhotoPosition = position
        openCamera()
    }
    override fun onAnswered(position: Int) {
        MoveToNextPosition(topicRlv.layoutManager?:return)
    }

    var count=0
    private var uploadManager: UploadManager? = null
    private fun uploadFile(total: Int, e: Exercise, token: String, file: File) {
        if (this.uploadManager == null) {
            this.uploadManager = UploadManager(Configuration.Builder().build(), 3)
        }
        uploadManager?.put(file, null, token, { _, info, response ->
            count++
            //res包含hash、key等信息，具体字段取决于上传策略的设置
            if(info.isOK) {
                val hash = response.getString("key")
                val key = response.getString("hash")
                // insert to db
                e.localUrl = null
                e.answerFileUrl = key
                e.save(object : SaveListener<String>() {
                    override fun done(objectId: String?, e: BmobException?) {
                        if (e == null) {
                            toasty("上传图片成功")
                        } else {
                            toasty("上传图片失败：${e.message}")
                        }
                        if (count == total) {
                            scoreFinish()
                        }
                    }
                })
            } else {
                toasty("上传图片失败")
            }
        }, null)
    }

    /**
     * @param key 覆盖的文件名称
     */
    private fun getQnToken(key: String? = null): String {
        val bucket = "studycollection"
        val auth = Auth.create(Configs.ak, Configs.sk)
        return auth.uploadToken(bucket, key)
    }

    private fun scoreFinish() {
        // 积分只计算小题
        val es = mExercises.filter { it.answer != null}
        var score = 0
        for (e in es) {
            if (e.answer?.trim()?.first() == e.topic?.solution?.trim()?.first()) {
                score++
            }
        }
        //
        pDialog.dismiss()
        val sDiloag = SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("提交完成")
            .setContentText("获得 ${score} 积分")
            .setConfirmText("OK")
            .showCancelButton(false)
            .setConfirmClickListener {

                val user = BmobUser.getCurrentUser(User::class.java)
                user.integral = (user.integral?:0)+score
                user.update(object : UpdateListener() {
                    override fun done(e: BmobException?) {
                        if (e == null) {
                            EventBus.getDefault().post(EventUpdateSettingDisplay())
                            startActivity(Intent(this@AnswerActivity, LotteryActivity::class.java))
                            it.cancel()
                            finish()
                        } else {
                            ToastUtils.show("积分获取失败~")
                        }
                    }
                })
            }
        sDiloag.setCancelable(false)
        sDiloag.setCanceledOnTouchOutside(false)
        sDiloag.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_CAMERA -> {
                    if (mTmpPhotoFilePath != null) {
                        mExercises.get(mCurrentTakePhotoPosition!!).localUrl = mTmpPhotoFilePath
                        mAdapter?.notifyItemChanged(mCurrentTakePhotoPosition!!)
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}