package com.hegetomi.moneytracker

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.lang.Exception

class DeleteAllTransactionDialog : DialogFragment() {

interface TransactionDeleter{
    fun deleteAll()
}

    private lateinit var transactionDeleter: TransactionDeleter


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is TransactionDeleter){
            transactionDeleter = context
        } else{
            throw Exception("Error!")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage("All data will be lost! Do you want to continue?")
        dialogBuilder.setTitle("Delete all data?")
            .setPositiveButton("Yes") { _, _ ->
                transactionDeleter.deleteAll()
            }
            .setNegativeButton("Cancel") {
                    _, _ ->

            }
        return dialogBuilder.create()

    }

}