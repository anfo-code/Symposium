package com.example.symposium.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.symposium.R
import com.example.symposium.databinding.ActivityAddBoardBinding
import com.example.symposium.utils.BaseActivity

class AddBoardActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityAddBoardBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityAddBoardBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setToolbar()
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbarAddBoard)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarAddBoard.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.clAddBoardMain -> {
                hideKeyboard(this)
            }
        }
    }
}