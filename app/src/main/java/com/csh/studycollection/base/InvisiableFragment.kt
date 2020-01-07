package com.csh.studycollection.base

import android.os.Bundle
import androidx.fragment.app.Fragment

class InvisiableFragment: Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

    }
}