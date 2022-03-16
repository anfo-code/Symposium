package com.example.symposium.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.symposium.R
import com.example.symposium.databinding.ActivityChangeUsersDataBinding
import com.example.symposium.models.User
import com.example.symposium.utils.BaseActivity
import com.example.symposium.utils.Constants

class ChangeUsersDataActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityChangeUsersDataBinding
    private lateinit var dataType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityChangeUsersDataBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setDataType()
        setToolbar()
        setHint()
        firestoreHandler.getUserData(this)

        binding.clChangeUsersDataMain.setOnClickListener(this)
    }

    private fun setDataType() {
        dataType = intent.getStringExtra("DataType").toString()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.clChangeUsersDataMain -> {
                hideKeyboard(this)
            }
        }
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbarChangeUsersData)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarChangeUsersData.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.tvToolbarText.text = dataType
    }

    private fun setHint() {
        when (dataType) {
            Constants.EMAIL -> {
                binding.tvHint.text = getString(R.string.change_your_email)
            }
            Constants.PHONE -> {
                binding.tvHint.text = getString(R.string.change_your_phone)
            }
            Constants.NAME -> {
                binding.tvHint.text = getString(R.string.change_your_name)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.confirm_button, menu)

        menu!!.getItem(0).isVisible = true

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.confirm_button -> {
                changeData()
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun setCurrentDataInEditText(user: User) {
        when (dataType) {
            Constants.EMAIL -> {
                binding.etData.setText(user.email)
            }
            Constants.PHONE -> {
                binding.etData.setText(checkNumber(user.mobile.toString()))
            }
            Constants.NAME -> {
                binding.etData.setText(user.name)
            }
        }
    }

    private fun changeData(): Boolean {
        when (dataType) {
            Constants.NAME -> firestoreHandler.changeName(binding.etData.text.toString())
            Constants.EMAIL -> firestoreHandler.changeEmail(binding.etData.text.toString())
            Constants.PHONE -> firestoreHandler.changePhone(binding.etData.text.toString().toLong())
        }

        return true
    }
}