package com.hegetomi.moneytracker.transaction

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hegetomi.moneytracker.AppDatabase
import com.hegetomi.moneytracker.ui.MainActivity
import com.hegetomi.moneytracker.R
import com.hegetomi.moneytracker.touch.TransactionTouchHandlerCallback
import kotlinx.android.synthetic.main.transaction_row.view.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class TransactionAdapter(
    val context: Context,
    transactionList: List<Transaction>
) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>(),
    TransactionTouchHandlerCallback {

    private var transactionHolder: ArrayList<Transaction> = ArrayList()

    init {
        transactionHolder.addAll(transactionList)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var category: TextView = itemView.tvCategory
        var price: TextView = itemView.tvPrice
        var icon: ImageView = itemView.ivIcon
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val transactionView =
            LayoutInflater.from(context).inflate(R.layout.transaction_row, parent, false)

        return ViewHolder(transactionView)
    }

    override fun getItemCount(): Int = transactionHolder.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transactionItem = transactionHolder[position]
        holder.category.text = transactionItem.category
        val priceText = transactionItem.price.toString() + "$"
        holder.price.text = priceText
        holder.icon.setImageResource(
            when (transactionItem.isIncome) {
                true -> R.drawable.ic_baseline_add_circle_24
                false -> R.drawable.ic_baseline_remove_circle_24
            }
        )
    }

    fun addTransaction(transaction: Transaction) {
        transactionHolder.add(transaction)
        notifyItemInserted(transactionHolder.lastIndex)
    }

    override fun onDismissed(position: Int) {
        deleteItemBasedOnPosition(position)
    }

    private fun deleteItemBasedOnPosition(position: Int) {
        try {
            val transactionToDelete = transactionHolder[position]
            Thread {
                AppDatabase.getInstance(
                    context
                ).transactionDAO()
                    .deleteTransaction(transactionToDelete)
                (context as MainActivity).runOnUiThread {
                    transactionHolder.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, itemCount)
                }
            }.start()
        } catch (e: Exception) {
        }

    }

    fun deleteAll() {
        try {
            for (i in transactionHolder.size - 1 downTo 0) {
                val toBuyToDelete = transactionHolder[i]
                Thread {
                    AppDatabase.getInstance(
                        context
                    ).transactionDAO().deleteTransaction(toBuyToDelete)
                }.start()
            }
        } catch (e: Exception) {
        } finally {
            transactionHolder.clear()
            notifyDataSetChanged()
        }
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(transactionHolder, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }
}