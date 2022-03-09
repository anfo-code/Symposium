package com.example.symposium.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.example.symposium.R
import com.example.symposium.databinding.ActivitySignInBinding
import com.example.symposium.firebase.FirestoreHandler
import com.example.symposium.models.User
import com.example.symposium.utils.BaseActivity
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


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
        binding.ivGoogleSignIn.setOnClickListener(this)
        binding.ivFacebookSignIn.setOnClickListener(this)
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
            R.id.ivGoogleSignIn -> {
                signInViaGoogle()
            }
            R.id.ivFacebookSignIn -> {
                facebookLogIn()
                finish()
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
                        showErrorSnackBar("Authentication Failed")
                        cancelProgressDialog()
                        Log.e("Error", task.exception!!.message.toString())
                    }
                }
        }
    }

    private fun signInViaGoogle() {
        showProgressDialog("Please wait")
        val signInIntent = googleSignInBuilder().signInIntent
        resultLauncherGoogle.launch(signInIntent)

    }

    private var resultLauncherGoogle =
        registerForActivityResult(
            ActivityResultContracts
                .StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data

                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    Log.i("TAG", "firebaseAuthWithGoogle ${account.id}")
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    showErrorSnackBar("Google sign in failed")
                    Log.w("GOOGLE SIGN IN", e)
                }
            }
        }

    private fun firebaseAuthWithGoogle(idToken: String) {
        Log.i("AUTHENTICATION", "SUCCESS")
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    FirestoreHandler().signInUser(this)

                } else {
                    showErrorSnackBar("Authentication Failed")
                    cancelProgressDialog()
                    Log.e("Error", task.exception!!.message.toString())
                }
            }
    }

    private fun facebookLogIn() {
       startActivity(Intent(this, FacebookAuthenticationActivity::class.java))
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

    private fun setToolbar() {
        setSupportActionBar(binding.toolbarSignIn)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarSignIn.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}