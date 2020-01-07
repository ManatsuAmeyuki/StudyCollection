package com.csh.studycollection.view

import android.content.Context
import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.csh.studycollection.utils.DensityUtil

class GridImageDecoration(val mCxt: Context, var count: Int, var spacelr: Float, var spacetop: Float)
    : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view) // item position
        val column = position % count // item column
        outRect.left = DensityUtil.dip2px(mCxt, spacelr)
        outRect.top =  DensityUtil.dip2px(mCxt, spacetop)
        if (column == count - 1) {
            outRect.right =  DensityUtil.dip2px(mCxt, spacelr)
        } else {
            outRect.right = 0
        }
    }

}