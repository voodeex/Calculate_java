package com.example.jsonlab10

import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderAdapter : RecyclerView.Adapter<OrderAdapter.Row>() {
    private var orders = emptyList<Order>()

    fun show(rows: List<Order>) {
        orders = rows
        notifyDataSetChanged()
    }

    override fun getItemCount() = orders.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Row {
        val context = parent.context
        val padding = (10 * context.resources.displayMetrics.density).toInt()
        val title = TextView(context).apply { textSize = 18f }
        val details = TextView(context).apply { textSize = 15f }
        val container = LinearLayout(context).apply {
            layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            orientation = LinearLayout.VERTICAL
            setPadding(0, padding, 0, padding)
            addView(title)
            addView(details)
        }
        return Row(container, title, details)
    }

    override fun onBindViewHolder(holder: Row, position: Int) {
        val order = orders[position]
        val name = order.customer?.name?.takeIf { it.isNotBlank() } ?: "Клиент не указан"
        val count = order.items.orEmpty().count { !it.isNullOrBlank() }
        holder.title.text = "${order.number} · ${money(requireNotNull(order.total))}"
        holder.details.text = "$name · Позиций: $count"
    }

    class Row(container: LinearLayout, val title: TextView, val details: TextView) :
        RecyclerView.ViewHolder(container)
}
