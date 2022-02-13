package com.example.symposium.activities

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.symposium.R
import com.example.symposium.databinding.SignInActivityBinding
import com.example.symposium.utils.Helper


class SignInActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: SignInActivityBinding
    private val helper = Helper()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = SignInActivityBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setToolbar()

        binding.buttonSignIn.setOnClickListener(this)
        binding.constraintLayoutSignIn.setOnClickListener(this)

        helper.setFullScreen(window)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.buttonSignIn -> {
                Toast.makeText(this, "Click", Toast.LENGTH_SHORT).show()
                helper.hideKeyboard(this)
            }
            R.id.constraintLayoutSignIn -> {
                helper.hideKeyboard(this)
            }
        }
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbarSignIn)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarSignIn.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}