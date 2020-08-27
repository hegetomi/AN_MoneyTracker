package com.hegetomi.moneytracker.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hegetomi.moneytracker.R
import kotlinx.android.synthetic.main.activity_password_screen.*
import java.security.MessageDigest


class PasswordScreen : AppCompatActivity() {

    companion object {
        private const val salt = "\\qW46p_K!sh"
        private var digest = MessageDigest.getInstance("SHA-256")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_screen)

        val firstrun = isFirstRun()

        civCode.inPasswordMode = true
        civCode.setEditable(true)

        civCode.addOnCompleteListener { code ->

            when {
                firstrun -> {
                    savePassword()
                    goToMain()
                }
                getPassword().equals(bytesToHex(digest.digest((salt + code).toByteArray()))) -> {
                    goToMain()
                }
                else -> {
                    civCode.error = "Invalid password, try again."
                    civCode.setEditable(true)
                    civCode.code = ""
                }
            }
        }
        if (firstrun) {
            tvPasswordText.text = getString(R.string.setup_password)
        } else {
            tvPasswordText.text = getString(R.string.enter_password)
        }

    }

    private fun isFirstRun(): Boolean {

        val sp = getSharedPreferences("My Settings", Context.MODE_PRIVATE)
        return sp.getBoolean("FIRST_RUN", true)

    }

    private fun savePassword() {
        val preferenceName = "My Settings"
        val sp = getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString(
            "PASSWORD",
            bytesToHex(digest.digest((Companion.salt + civCode.code).toByteArray()))
        )
        editor.apply()
    }

    private fun getPassword(): String? {
        val sp = getSharedPreferences("My Settings", Context.MODE_PRIVATE)
        return sp.getString("PASSWORD", "0000")

    }

    private fun goToMain() {
        val intent = Intent()
        intent.setClass(this, MainActivity::class.java)
        MainActivity.wasInBackground = false
        startActivity(intent)
    }

    private fun bytesToHex(hash: ByteArray): String? {
        val hexString = StringBuffer()
        for (i in hash.indices) {
            val hex = Integer.toHexString(0xff and hash[i].toInt())
            if (hex.length == 1) hexString.append('0')
            hexString.append(hex)
        }
        return hexString.toString()
    }



}
