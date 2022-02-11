package com.example.symposium

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.symposium.databinding.SignUpActivityBinding

class SignUpActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: SignUpActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = SignUpActivityBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setToolbar()
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbarSignUp)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarSignUp.setNavigationOnClickListener{
            onBackPressed()
        }

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.buttonSignUp -> {
                Toast.makeText(this, "click", Toast.LENGTH_SHORT).show()
            }
        }
    }


}