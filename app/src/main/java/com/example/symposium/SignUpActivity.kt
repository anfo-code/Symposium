package com.example.symposium

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.symposium.databinding.SignUpActivityBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding : SignUpActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = SignUpActivityBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.buttonSignUp.setOnClickListener {
            Toast.makeText(this, "click", Toast.LENGTH_SHORT).show()
        }
    }
}