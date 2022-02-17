package com.example.symposium.activities

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.symposium.R
import com.example.symposium.databinding.SignInActivityBinding
import com.example.symposium.utils.Helper
import com.google.firebase.auth.FirebaseAuth


class SignInActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: SignInActivityBinding
    private val helper = Helper()
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = SignInActivityBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        setToolbar()

        binding.buttonSignIn.setOnClickListener(this)
        binding.constraintLayoutSignIn.setOnClickListener(this)

        helper.setFullScreen(window)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.buttonSignIn -> {
                signIn()
                helper.hideKeyboard(this)
            }
            R.id.constraintLayoutSignIn -> {
                helper.hideKeyboard(this)
            }
        }
    }

    private fun signIn() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        if (validateForm(email, password)) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        showErrorSnackBar("Oops! Something went wrong! Please, try again!")
                        Log.e("Error", task.exception!!.message.toString())
                    }
                }
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter a email")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter a password")
                false
            }
            else -> {
                Log.i("ELSE BLOCK", "ACTIVATED")
                true
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