package `in`.amankumar110.currencyconverterapp.utils

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SpacingItemDecoration(
    context: Context,
    private val verticalSpace: Int,
    private val horizontalSpace: Int
) : RecyclerView.ItemDecoration() {

    private val verticalSpacePx: Int =
        (verticalSpace * context.resources.displayMetrics.density).toInt()

    private val horizontalSpacePx: Int =
        (horizontalSpace * context.resources.displayMetrics.density).toInt()

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val spanCount = (parent.layoutManager as? GridLayoutManager)?.spanCount ?: return

        val column = position % spanCount
        val row = position / spanCount

        // Set horizontal spacing
        // Left: Distribute space proportionally based on column index
        outRect.left = if (column == 0) 0 else horizontalSpacePx / 2
        // Right: Distribute space proportionally based on column index
        outRect.right = if (column == spanCount - 1) 0 else horizontalSpacePx / 2

        // Set vertical spacing
        if (row > 0) {
            outRect.top = verticalSpacePx
        }
    }
}
