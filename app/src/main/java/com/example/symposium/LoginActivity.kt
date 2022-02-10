package com.example.symposium


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.symposium.databinding.LoginActivityBinding


class LoginActivity : AppCompatActivity(), View.OnClickListener {
        lateinit var binding : LoginActivityBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        cancelNightMode()
        val splashScreen = installSplashScreen()
        binding = LoginActivityBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)



        binding.buttonSignUp.setOnClickListener(this)
        binding.buttonSignIn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.buttonSignIn -> {
                Toast.makeText(this, "CLICK", Toast.LENGTH_SHORT).show()
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