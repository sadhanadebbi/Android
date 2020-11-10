/*
 * Copyright (c) 2019 DuckDuckGo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duckduckgo.app.tabs.ui

import android.graphics.Canvas
import androidx.core.math.MathUtils
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.duckduckgo.app.tabs.model.TabEntity
import kotlin.math.abs

class SwipeToCloseTabListener(
    private val tabSwitcherAdapter: TabSwitcherAdapter,
    private val numberGridColumns: Int,
    private val tabSwipedListener: OnTabSwipedListener
) :
    ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.START or ItemTouchHelper.END or ItemTouchHelper.UP or ItemTouchHelper.DOWN,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {

    interface OnTabSwipedListener {
        fun onSwiped(tab: TabEntity)
        fun onSwapTabs(fromTabEntity: TabEntity, toTabEntity: TabEntity)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val tab = tabSwitcherAdapter.getTab(viewHolder.adapterPosition)
        tabSwipedListener.onSwiped(tab)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val alpha = 1 - (abs(dX) / (recyclerView.width / numberGridColumns))
            viewHolder.itemView.alpha = MathUtils.clamp(alpha, 0f, 1f)
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)

        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            viewHolder?.itemView?.alpha = 0.7f
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.alpha = 1f
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        val from = viewHolder.adapterPosition
        val to = target.adapterPosition

        if (from >= 0 && to >= 0) {
            tabSwipedListener.onSwapTabs(tabSwitcherAdapter.getTab(from), tabSwitcherAdapter.getTab(to))
        }
        return false
    }
}
