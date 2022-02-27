package com.example.symposium.activities


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.symposium.R
import com.example.symposium.databinding.ActivityLoginBinding
import com.example.symposium.utils.BaseActivity


class LoginActivity : BaseActivity(), View.OnClickListener {
       private lateinit var binding : ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {

        cancelNightMode()

        val splashScreen = installSplashScreen()

        if (getCurrentUserID().isNotEmpty()){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setFullScreen(window)

        binding.buttonSignUp.setOnClickListener(this)
        binding.buttonSignIn.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.buttonSignIn -> {
                startActivity(Intent(this, SignInActivity::class.java))
            }
            R.id.buttonSignUp -> {
                startActivity(Intent(this, SignUpActivity::class.java))
            }
        }
    }

    private fun cancelNightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}