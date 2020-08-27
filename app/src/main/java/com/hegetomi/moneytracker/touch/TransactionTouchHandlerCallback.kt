package com.hegetomi.moneytracker.touch

interface TransactionTouchHandlerCallback {

    fun onDismissed(position: Int)
    fun onItemMoved(fromPosition: Int, toPosition: Int)
}