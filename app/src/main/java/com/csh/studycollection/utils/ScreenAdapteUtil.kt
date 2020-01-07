package com.csh.studycollection.utils

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks
import android.content.res.Configuration

/**
 * created by shenghuiche on 2019/3/6
 */
object ScreenAdapteUtil {

    private var sNoncompatDensity: Float = 0.0f
    private var sNoncompatScaleDensity: Float = 0.0f

    /**
     *  屏幕dpi =（ √（屏幕宽² + 屏幕高²））/ 屏幕英寸
     *
     *  px = density * dp;
     *  density = dpi / 160;
     *  px = dp * (dpi / 160);
     *
     *  designDp = 设计图的屏幕宽或高/(设计图的屏幕dpi/160)
     */
    fun setCustomDensity(activity: Activity, application: Application, designDp: Int = 360) {
        val appDisplayMetrics = application.resources.displayMetrics

        if (sNoncompatDensity == 0.0f) {
            sNoncompatDensity = appDisplayMetrics.density
            sNoncompatScaleDensity = appDisplayMetrics.scaledDensity

            application.registerComponentCallbacks(object : ComponentCallbacks {
                override fun onLowMemory() {}

                override fun onConfigurationChanged(newConfig: Configuration?) {
                    if (newConfig != null && newConfig.fontScale > 0) {
                        sNoncompatScaleDensity = application.resources.displayMetrics.scaledDensity
                    }
                }
            })

            val targetDensity = appDisplayMetrics.widthPixels * 1.0f / designDp
            val targetScaleDensity = targetDensity * (sNoncompatScaleDensity / sNoncompatDensity)
            val targetDensityDpi = (160 * targetDensity).toInt()

            appDisplayMetrics.density = targetDensity
            appDisplayMetrics.scaledDensity = targetScaleDensity
            appDisplayMetrics.densityDpi = targetDensityDpi

            val activityDisplayMetrics = activity.resources.displayMetrics
            activityDisplayMetrics.density = targetDensity
            activityDisplayMetrics.scaledDensity = targetScaleDensity
            activityDisplayMetrics.densityDpi = targetDensityDpi
        }
    }
}