package com.example.symposium.activities

import android.os.Bundle
import com.example.symposium.databinding.ActivityAccountBinding
import com.example.symposium.utils.BaseActivity

class AccountActivity : BaseActivity() {

    private lateinit var binding: ActivityAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAccountBinding.inflate(layoutInflater)
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