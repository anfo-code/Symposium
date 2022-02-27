package com.example.symposium.activities

import android.os.Bundle
import com.example.symposium.databinding.ActivitySettingsBinding
import com.example.symposium.utils.BaseActivity

class SettingsActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivitySettingsBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        setToolbar()
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbarSettings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarSettings.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}