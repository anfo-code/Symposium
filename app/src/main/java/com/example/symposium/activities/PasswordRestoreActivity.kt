package com.example.symposium.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.symposium.R
import com.example.symposium.databinding.ActivityPasswordRestoreActicityBinding
import com.example.symposium.utils.BaseActivity
import com.google.firebase.auth.FirebaseAuth

class PasswordRestoreActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityPasswordRestoreActicityBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityPasswordRestoreActicityBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setToolbar()

        binding.cvPasswordRestore.setOnClickListener(this)
        binding.buttonSendRequest.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.cvPasswordRestore -> {
                hideKeyboard(this)
            }
            R.id.buttonSendRequest -> {
                sendRestoreRequest()
            }
        }
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbarRestorePassword)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarRestorePassword.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun sendRestoreRequest() {
        showProgressDialog(getString(R.string.please_wait))
        validateEmail()
    }

    private fun validateEmail() {
        if (binding.etEmail.text!!.isNotEmpty()) {
            val firebase = FirebaseAuth.getInstance()
            cancelProgressDialog()
            firebase.sendPasswordResetEmail(binding.etEmail.text.toString())
                .addOnCompleteListener {
                    Toast.makeText(
                        this,
                        "Password Restoration Request was sent, check your email for further steps!",
                        Toast.LENGTH_LONG
                    ).show()
                    cancelProgressDialog()
                    finish()
                }
        } else {
            showErrorSnackBar("Please, enter your email!")
            cancelProgressDialog()
        }
    }
}