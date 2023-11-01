package com.example.grandatmahotel.utils

import android.content.Context
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.example.grandatmahotel.R


class CustomTableRows<T>(
    private val context: Context,
    private val data: List<T>,
    private val tableLayout: TableLayout,
    private val rowData: (cell: T, index: Int) -> List<String>
) {
    fun render() {
        // paddings
        val density = context.resources.displayMetrics.density
        val paddingX = 16 * density.toInt()
        val paddingY = 12 * density.toInt()

        // clear table (from 2nd row)
        tableLayout.removeViews(1, tableLayout.childCount - 1)

        for ((index, item) in data.withIndex()) {
            val row = TableRow(context)
            val layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
            )
            row.layoutParams = layoutParams
            val rowData = rowData(item, index)
            val cellParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )

            for (cellData in rowData) {
                val cell = TextView(context)
                cell.layoutParams = cellParams
                cell.text = cellData
                cell.setPadding(paddingX, paddingY, paddingX, paddingY)
                cell.typeface = context.resources.getFont(R.font.gabarito)
                row.addView(cell)
            }
            tableLayout.addView(row)
        }
    }
}