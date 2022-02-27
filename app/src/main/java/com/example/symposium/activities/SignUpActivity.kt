package com.example.symposium.activities

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.symposium.R
import com.example.symposium.databinding.ActivitySignUpBinding
import com.example.symposium.firebase.FirestoreHandler
import com.example.symposium.models.User
import com.example.symposium.utils.BaseActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivitySignUpBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignUpBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setToolbar()
        binding.buttonSignUp.setOnClickListener(this)
        binding.constraintLayoutSignUp.setOnClickListener(this)
    }

    fun userRegisteredSuccess() {
        Toast.makeText(
            this,
            "you have successfully registered ",
            Toast.LENGTH_LONG
        ).show()
        cancelProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()
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
                hideKeyboard(this)
            }
            R.id.constraintLayoutSignUp -> {
                hideKeyboard(this)
            }
        }
    }

    private fun registerUser() {
        val name = binding.etName.text.toString().trim { it <= ' ' }
        val email = binding.etEmail.text.toString().trim { it <= ' ' }
        val password = binding.etPassword.text.toString()
        val repeatedPassword = binding.etPasswordRepeat.text.toString()

        if (validateForm(name, email, password, repeatedPassword)) {
            val progressDialogText = resources.getString(R.string.please_wait)
            showProgressDialog(progressDialogText)
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val user = User(firebaseUser.uid, name, email)

                        FirestoreHandler().registerUser(this, user)
                    } else {
                        showErrorSnackBar("Oops! Something went wrong! Please, try again!")
                        Log.e("Error", task.exception!!.message.toString())
                    }
                }
        }
    }

    private fun validateForm(
        name: String, email: String, password: String, repeatedPassword: String
    ): Boolean {
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
            password != repeatedPassword -> {
                showErrorSnackBar("Entered passwords don't match!")
                Log.i("PASSWORDS", (password != repeatedPassword).toString())
                Log.i("PASSWORDS", ("$password $repeatedPassword"))
                false
            }
            else -> {
                Log.i("ELSE BLOCK", "ACTIVATED")
                true
            }
        }
    }
}