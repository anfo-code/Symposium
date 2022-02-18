package com.example.symposium.activities

import android.os.Bundle
import android.view.View
import com.example.symposium.databinding.MainActivityBinding
import com.example.symposium.utils.BaseActivity

class MainActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = MainActivityBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setFullScreen(window)

        setToolbar()
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbarMain)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarMain.setNavigationOnClickListener {

        }
    }
}