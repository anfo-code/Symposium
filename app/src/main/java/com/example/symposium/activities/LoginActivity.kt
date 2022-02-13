package com.example.symposium.activities


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.symposium.R
import com.example.symposium.databinding.LoginActivityBinding


class LoginActivity : BaseActivity(), View.OnClickListener {
       private lateinit var binding : LoginActivityBinding


    override fun onCreate(savedInstanceState: Bundle?) {

        cancelNightMode()

        val splashScreen = installSplashScreen()

        binding = LoginActivityBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setFullScreen()

        binding.buttonSignUp.setOnClickListener(this)
        binding.buttonSignIn.setOnClickListener(this)

    }

    private fun setFullScreen() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.buttonSignIn -> {
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
            }
            R.id.buttonSignUp -> {
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun cancelNightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}