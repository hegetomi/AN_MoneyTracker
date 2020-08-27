package com.hegetomi.moneytracker.ui

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import com.hegetomi.moneytracker.AppDatabase
import com.hegetomi.moneytracker.R
import kotlinx.android.synthetic.main.activity_balance_screen.*

class BalanceScreen : AppCompatActivity() {
    private var WAS_IN_BACK: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_balance_screen)

        WAS_IN_BACK = intent.getBooleanExtra("WAS_IN_BACK",true)

        var incomes: Double
        var expenses: Double

        val query = Thread {
            val getIncomes = AppDatabase.getInstance(
                this
            ).transactionDAO().getIncomes()
            val getExpenses = AppDatabase.getInstance(
                this
            ).transactionDAO().getExpenses()
            runOnUiThread {
                incomes = getIncomes
                expenses = getExpenses

                val incomeText = SpannableString("Income: $incomes$")
                incomeText.setSpan(
                    ForegroundColorSpan(Color.GREEN), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                tvIncomes.text = incomeText

                val expenseText = SpannableString("Expenses: $expenses$")
                expenseText.setSpan(
                    ForegroundColorSpan(Color.RED), 0, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                tvExpenditures.text = expenseText

                val balanceText = SpannableString("Balance: ${incomes - expenses}$")
                balanceText.setSpan(
                    ForegroundColorSpan(
                        if (incomes - expenses >= 0 ) { Color.GREEN } else{ Color.RED }
                ), 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                tvBalance.text = balanceText
            }
        }
        query.start()


    }

    override fun onStop() {
        super.onStop()
        WAS_IN_BACK = true
    }
    override fun onPause() {
        super.onPause()
        WAS_IN_BACK = true
        MainActivity.wasInBackground = false
    }

    override fun onResume() {
        super.onResume()
        if (WAS_IN_BACK) {
            val intent = Intent()
            intent.setClass(this, PasswordScreen::class.java)
            startActivity(intent)
        }
    }
}