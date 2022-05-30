package com.example.symposium.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.example.symposium.R
import com.example.symposium.databinding.ActivitySignInBinding
import com.example.symposium.firebase.FirestoreHandler
import com.example.symposium.utils.BaseActivity
import com.google.firebase.auth.FirebaseAuth


open class SignInActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySignInBinding
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignInBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        setToolbar()

        binding.buttonSignIn.setOnClickListener(this)
        binding.constraintLayoutSignIn.setOnClickListener(this)
        binding.tvForgotPassword.setOnClickListener(this)
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
            R.id.tvForgotPassword -> {
                sendResetPasswordEmail()
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
                    }
                }
                .addOnFailureListener { task ->
                    task.printStackTrace()
                    cancelProgressDialog()
                    showErrorSnackBar(task.localizedMessage!!)
                }
        }
    }

    fun signInSuccess() {
        cancelProgressDialog()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun validateForm(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter a email")
                cancelProgressDialog()
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter a password")
                cancelProgressDialog()
                false
            }
            else -> true
        }
    }

    private fun sendResetPasswordEmail() {
        startActivity(Intent(this, PasswordRestoreActivity::class.java))
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbarSignIn)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarSignIn.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}