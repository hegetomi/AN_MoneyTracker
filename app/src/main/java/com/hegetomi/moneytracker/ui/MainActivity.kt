package com.hegetomi.moneytracker.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import com.hegetomi.moneytracker.AppDatabase
import com.hegetomi.moneytracker.DeleteAllTransactionDialog
import com.hegetomi.moneytracker.R
import com.hegetomi.moneytracker.touch.TransactionRecyclerTouchCallback
import com.hegetomi.moneytracker.transaction.Transaction
import com.hegetomi.moneytracker.transaction.TransactionAdapter
import kotlinx.android.synthetic.main.activity_main.*
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetSequence
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal
import java.util.*

class MainActivity : AppCompatActivity(), DeleteAllTransactionDialog.TransactionDeleter {
    private lateinit var transactionAdapter: TransactionAdapter

    companion object {
        var wasInBackground = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRecycler()

        setupSaveButton()

        if (isFirstRun()) {
            showTutorial()
            addDummy()
        }
        saveLastRun()

    }

    private fun initRecycler() {
        Thread {
            val transactionList =
                AppDatabase.getInstance(this@MainActivity)
                    .transactionDAO().getTransaction()
            runOnUiThread {
                transactionAdapter =
                    TransactionAdapter(this, transactionList)
                recTransaction.adapter = transactionAdapter
                val touchCallback = TransactionRecyclerTouchCallback(transactionAdapter)
                val itemTouchHelper = ItemTouchHelper(touchCallback)
                itemTouchHelper.attachToRecyclerView(recTransaction)
                val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
                recTransaction.addItemDecoration(itemDecoration)
            }
        }.start()

    }

    private fun transactionCreated(transaction: Transaction) {
        Thread {
            AppDatabase.getInstance(this@MainActivity)
                .transactionDAO()
                .insertTransaction(transaction)
            runOnUiThread {
                transactionAdapter.addTransaction(transaction)
            }
        }.start()

    }

    private fun setupSaveButton() {
        btnSave.setOnClickListener {
            val category = etName.text.toString()
            val price = etPrice.text.toString()
            val isIncome = tbType.isChecked

            if (checkInput(category, price)) {
                transactionCreated(
                    Transaction(
                        null,
                        category,
                        price.toDouble(),
                        isIncome
                    )
                )
                etName.text.clear()
                etPrice.text.clear()
            }
        }
    }

    private fun checkInput(category: String, price: String): Boolean {

        return if (category.isEmpty() && price.isEmpty()) {
            Toast.makeText(this, "Please enter a category and a price", Toast.LENGTH_LONG).show()
            false
        } else if (category.isEmpty()) {
            Toast.makeText(this, "Please enter a category", Toast.LENGTH_LONG).show()
            false
        } else if (price.isEmpty()) {
            Toast.makeText(this, "Please enter a price", Toast.LENGTH_LONG).show()
            false
        } else {
            true
        }
    }

    private fun saveLastRun() {
        val prefName = "My Settings"
        val sp = getSharedPreferences(prefName, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString("LAST_RUN", Date(System.currentTimeMillis()).toString())
        editor.putBoolean("FIRST_RUN", false)
        editor.apply()
    }

    private fun isFirstRun(): Boolean {
        val sp = getSharedPreferences("My Settings", Context.MODE_PRIVATE)
        return sp.getBoolean("FIRST_RUN", true)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_layout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.wipe_data -> {
                DeleteAllTransactionDialog().show(supportFragmentManager, "")
                true
            }
            R.id.view_balance -> {
                val intent = Intent()
                intent.setClass(
                    this,
                    BalanceScreen::class.java
                )
                intent.putExtra("WAS_IN_BACK", false)
                startActivity(intent)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        wasInBackground = true
    }

    override fun onResume() {
        super.onResume()
        Log.i("inback", wasInBackground.toString())
        if (wasInBackground) {
            val intent = Intent()
            intent.setClass(this, PasswordScreen::class.java)
            startActivity(intent)
        }

    }

    private fun showTutorial() {
        MaterialTapTargetSequence()
            .addPrompt(
                CustomPromptBuilder(this)
                    .setTarget(R.id.etName)
                    .setPromptBackground(RectanglePromptBackground())
                    .setPromptFocal(RectanglePromptFocal())
                    .setPrimaryText("Transaction category")
                    .setSecondaryText("Enter transaction category")
            )
            .addPrompt(
                CustomPromptBuilder(this)
                    .setTarget(R.id.etPrice)
                    .setPromptBackground(RectanglePromptBackground())
                    .setPromptFocal(RectanglePromptFocal())
                    .setPrimaryText("Transaction price")
                    .setSecondaryText("Enter transaction worth")
            )
            .addPrompt(
                CustomPromptBuilder(this)
                    .setTarget(R.id.recTransaction)
                    .setPromptBackground(RectanglePromptBackground())
                    .setPromptFocal(RectanglePromptFocal())
                    .setPrimaryText("Delete")
                    .setSecondaryText("Swipe left or right to delete items!")
            )
            .addPrompt(
                CustomPromptBuilder(this)
                    .setTarget(R.id.top)
                    .setPromptBackground(RectanglePromptBackground())
                    .setPromptFocal(RectanglePromptFocal())
                    .setPrimaryText("Options")
                    .setSecondaryText("See your balance, and other options here")
            )
            .show()

    }

    private fun addDummy() {
        transactionCreated(
            Transaction(
                null,
                "Demo income",
                50.0,
                true
            )
        )
        transactionCreated(
            Transaction(
                null,
                "Demo expense",
                40.0,
                false
            )
        )
    }

    override fun deleteAll() {
        transactionAdapter.deleteAll()
    }

}