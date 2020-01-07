package com.csh.studycollection.base

import android.content.Context
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.hjq.toast.ToastUtils


open class BaseBackgroundInit {
    var mCxt: Context? = null

    @VisibleForTesting
    var mInvisiableFragment: Lazy<InvisiableFragment>? = null

    constructor(@NonNull fact: FragmentActivity) {
        mCxt = fact
        mInvisiableFragment = getLazySingleton(fact.supportFragmentManager)
    }

    constructor(@NonNull fragment: Fragment) {
        mCxt = fragment.activity
        mInvisiableFragment = getLazySingleton(fragment.childFragmentManager)
    }

    @NonNull
    private fun getLazySingleton(@NonNull fragmentManager: FragmentManager): Lazy<InvisiableFragment> {
        return object : Lazy<InvisiableFragment> {

            private var fragment: InvisiableFragment? = null

            @Synchronized
            override fun get(): InvisiableFragment {
                if (fragment == null) {
                    fragment = getInVisiableFragment(fragmentManager)
                }
                return fragment as InvisiableFragment
            }

        }
    }

    private fun getInVisiableFragment(@NonNull fragmentManager: FragmentManager): InvisiableFragment? {
        var fragment: InvisiableFragment? = findInvisisableFragment(fragmentManager)
        val isNewInstance = fragment == null
        if (isNewInstance) {
            fragment = InvisiableFragment()
            fragmentManager
                    .beginTransaction()
                    .add(fragment, this.javaClass.name)
                    .commitNow()
        }
        return fragment
    }

    private fun findInvisisableFragment(@NonNull fragmentManager: FragmentManager): InvisiableFragment? {
        return fragmentManager.findFragmentByTag(this.javaClass.name) as InvisiableFragment?
    }

    protected fun toast(msg: String) {
        Toast.makeText(mInvisiableFragment?.get()?.activity, msg, Toast.LENGTH_SHORT).show()
    }

    protected fun longToast(msg: String) {
        Toast.makeText(mInvisiableFragment?.get()?.activity, msg, Toast.LENGTH_LONG).show()
    }

    protected fun toasty(msg: String?) {
        ToastUtils.show(msg)
    }

    @FunctionalInterface
    interface Lazy<V> {
        fun get(): V
    }

}