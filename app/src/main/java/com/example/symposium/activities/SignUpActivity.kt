package com.example.symposium.activities

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.symposium.R
import com.example.symposium.databinding.SignUpActivityBinding
import com.example.symposium.utils.Helper

class SignUpActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: SignUpActivityBinding
    private val helper = Helper()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = SignUpActivityBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setToolbar()
        helper.setFullScreen(window)
        binding.buttonSignUp.setOnClickListener(this)
        binding.constraintLayoutSignUp.setOnClickListener(this)
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbarSignUp)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarSignUp.setNavigationOnClickListener {
            onBackPressed()
        }
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.buttonSignUp -> {
                registerUser()
                helper.hideKeyboard(this)
            }
            R.id.constraintLayoutSignUp -> {
                helper.hideKeyboard(this)
            }
        }
    }

    private fun registerUser(){
        val name = binding.etName.text.toString().trim {it <= ' ' }
        val email = binding.etEmail.text.toString().trim {it <= ' ' }

        if (validateForm(name, email)) {
            Toast.makeText(this@SignUpActivity, "Now we can register a new user",
            Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateForm(name: String, email: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please enter a name")
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter a email")
                false
            }
            binding.etPassword.text.isNullOrEmpty() -> {
                showErrorSnackBar("Please enter a password")
                false
            }
            binding.etPassword !== binding.etPasswordRepeat -> {
                showErrorSnackBar("Entered passwords don't match!")
                false
            }
            else -> true
        }
    }
}