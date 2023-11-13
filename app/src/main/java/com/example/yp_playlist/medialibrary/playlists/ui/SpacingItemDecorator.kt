package com.example.yp_playlist.medialibrary.playlists.ui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SpacingItemDecorator(
    spacing: Int,
    private val margin: Int
) : RecyclerView.ItemDecoration() {

    private val halfSpacing = spacing / 2

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val columns = (parent.layoutManager as? GridLayoutManager)?.spanCount ?: 1
        val rows = parent.adapter?.itemCount?.div(columns) ?: 1

        val pos = parent.getChildAdapterPosition(view)

        fun setTopSpacing() {
            if (pos / columns + 1 == 1) {
                outRect.top = margin
            } else {
                outRect.top = halfSpacing
            }
        }

        fun setBottomSpacing() {
            if (pos / columns + 1 == rows) {
                outRect.bottom = margin
            } else {
                outRect.bottom = halfSpacing
            }
        }

        fun setLeftSpacing() {
            if (pos % columns == 0) {
                outRect.left = margin
            } else {
                outRect.left = halfSpacing
            }
        }

        fun setRightSpacing() {
            if (pos % columns == columns - 1) {
                outRect.right = margin
            } else {
                outRect.right = halfSpacing
            }
        }

        setTopSpacing()
        setBottomSpacing()
        setLeftSpacing()
        setRightSpacing()
    }
}