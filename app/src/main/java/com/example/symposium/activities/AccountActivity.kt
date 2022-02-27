package com.example.symposium.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.symposium.R
import com.example.symposium.databinding.AccountActivityBinding

class AccountActivity : AppCompatActivity() {

    private lateinit var binding: AccountActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = AccountActivityBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setToolbar()
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbarAccount)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarAccount.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}