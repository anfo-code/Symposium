package com.example.symposium.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.symposium.firebase.FirestoreHandler
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider

class FacebookAuthenticationActivity : SignInActivity() {

    private lateinit var callbackManager : CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        callbackManager = CallbackManager.Factory.create()

        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile"))
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    Log.i("TAG", "FACEBOOK LOGIN SUCCESS")
                    handleFacebookAccessToken(result.accessToken)
                }
                override fun onCancel() {
                    Log.i("TAG", "FACEBOOK LOGIN CANCELLED")
                }

                override fun onError(error: FacebookException) {
                    showErrorSnackBar("Facebook Authentication Failed")
                    Log.i("TAG", "FACEBOOK LOGIN FAILED")
                    error.printStackTrace()
                }
            })

    }

    fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}