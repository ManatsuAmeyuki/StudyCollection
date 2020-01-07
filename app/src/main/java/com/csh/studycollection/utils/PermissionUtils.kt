package com.csh.studycollection.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.afollestad.materialdialogs.MaterialDialog
import com.tbruyelle.rxpermissions2.RxPermissions


/**
 * created by shenghuiche on 2018/11/22
 */
object PermissionUtils {

    fun requestPermissionsEach(
        context: androidx.fragment.app.FragmentActivity,
        granted: (String) -> Unit,
        denied: ((String) -> Unit)?,
        unask: ((String) -> Unit)?,
        vararg permissions: String
    ) {
        val rxPermission = RxPermissions(context)
        val p = rxPermission.requestEach(*permissions)
            .subscribe { permission ->
                if (permission.granted) {
                    // `permission.name` is granted !
                    granted(permission.name)
//                        Log.e("===>", "${permission.name} is granted")
                } else if (permission.shouldShowRequestPermissionRationale) {
                    // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                    denied?.let { denied(permission.name) }
//                        Log.e("===>", "${permission.name} is denied")
                } else {
                    // 用户拒绝了该权限，并且选中『不再询问』
                    unask?.let { unask(permission.name) }
//                        Log.e("===>", "${permission.name} is never ask again")
                }
            }
    }

    fun requestEachCombined(
        context: androidx.fragment.app.FragmentActivity,
        granted: () -> Unit,
        denied: (() -> Unit)?,
        unask: (() -> Unit)?,
        vararg permissions: String
    ) {
        val rxPermission = RxPermissions(context)
        val p = rxPermission.request(*permissions)
            .subscribe { granted ->
                if (granted) {
                    // All permissions are granted !
                    granted()
//                        Log.e("===>", " All permissions are granted")
                } else {
                    // At least one denied permission
                    denied?.let { denied() }
//                        Log.e("===>", "At least one denied permission")
                }
            }
    }

    fun requestPermissions(
        context: androidx.fragment.app.FragmentActivity,
        granted: () -> Unit,
        atLeastOneDenied: (() -> Unit)?,
        atLeastOneDeniedWithUnask: (() -> Unit)?,
        vararg permissions: String
    ) {
        val rxPermission = RxPermissions(context)
        val p = rxPermission
            .requestEachCombined(*permissions)
            .subscribe { permission ->
                if (permission.granted) {
                    // All permissions are granted !
                    granted()
                } else if (permission.shouldShowRequestPermissionRationale) {
                    // At least one denied permission without ask never again
                    atLeastOneDenied?.let { atLeastOneDenied() }
                } else {
                    // At least one denied permission with ask never again
                    // Need to go to the settings
                    atLeastOneDeniedWithUnask?.let { atLeastOneDeniedWithUnask() }
                }
            }
    }

    fun gotoSettingPage(context: Context) {
        MaterialDialog.Builder(context)
            .title("提示")
            .content("开启相应权限")
            .positiveText("开启")
            .negativeText("暂不")
            .onPositive { dialog, which ->
                dialog.dismiss()
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            }
            .onNegative { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }


    fun toSelfSetting(context: Context) {
        val mIntent = Intent()
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= 9) {
            mIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
            mIntent.data = Uri.fromParts("package", context.packageName, null)
        } else if (Build.VERSION.SDK_INT <= 8) {
            mIntent.action = Intent.ACTION_VIEW
            mIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails")
            mIntent.putExtra("com.android.settings.ApplicationPkgName", context.packageName)
        }
        context.startActivity(mIntent)
    }

}
