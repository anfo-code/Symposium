package com.example.symposium.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.symposium.R
import com.example.symposium.databinding.ActivityAccountBinding
import com.example.symposium.models.User
import com.example.symposium.utils.BaseActivity
import com.example.symposium.utils.Constants
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception


class AccountActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAccountBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        setToolbar()
        firestoreHandler.getUserData(this)

        binding.ivEmailMore.setOnClickListener(this)
        binding.ivPhoneMore.setOnClickListener(this)
        binding.ivEditName.setOnClickListener(this)
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbarAccount)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarAccount.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun updateUserDetails(user: User) {
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding.ivAvatar)
        binding.tvAccountUserName.text = user.name
        binding.tvEmail.text = user.name
        binding.tvPhone.text = checkNumber(user.mobile.toString())
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivEmailMore -> {
                val intent = Intent(this, ChangeUsersDataActivity::class.java)
                intent.putExtra("DataType", Constants.EMAIL)
                resultLauncher.launch(intent)
            }
            R.id.ivPhoneMore -> {
                val intent = Intent(this, ChangeUsersDataActivity::class.java)
                intent.putExtra("DataType", Constants.PHONE)
                resultLauncher.launch(intent)
            }
            R.id.ivEditName -> {
                val intent = Intent(this, ChangeUsersDataActivity::class.java)
                intent.putExtra("DataType", Constants.NAME)
                resultLauncher.launch(intent)
            }
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                firestoreHandler.getUserData(this)
            }
        }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_account_menu, menu)

        menu?.getItem(0)?.isVisible = true

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.optionsMenuLogOut -> {
                logOut()
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logOut(): Boolean {
        //Firebase Log Out
        FirebaseAuth.getInstance().signOut()
        //Google Log Out
        GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).signOut()
        //Facebook Log Out
        LoginManager.getInstance().logOut()
        finish()
        startActivity(Intent(this, LoginActivity::class.java))
        return true
    }


}