package com.example.symposium.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.example.symposium.R
import com.example.symposium.databinding.SignInActivityBinding
import com.example.symposium.firebase.FirestoreHandler
import com.example.symposium.models.User
import com.example.symposium.utils.BaseActivity
import com.google.firebase.auth.FirebaseAuth


class SignInActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: SignInActivityBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = SignInActivityBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        setToolbar()

        binding.buttonSignIn.setOnClickListener(this)
        binding.constraintLayoutSignIn.setOnClickListener(this)

        setFullScreen(window)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.buttonSignIn -> {
                signIn()
                hideKeyboard(this)
            }
            R.id.constraintLayoutSignIn -> {
                hideKeyboard(this)
            }
        }
    }

    private fun signIn() {
        showProgressDialog("Please wait")
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        if (validateForm(email, password)) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        FirestoreHandler().signInUser(this)
                    } else {
                        showErrorSnackBar("Oops! Something went wrong! Please, try again!")
                        cancelProgressDialog()
                        Log.e("Error", task.exception!!.message.toString())
                    }
                }
        }
    }

    fun signInSuccess (user: User) {
        cancelProgressDialog()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
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