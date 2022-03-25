package com.example.symposium.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.symposium.R
import com.example.symposium.databinding.ActivityAccountBinding
import com.example.symposium.firebase.PhotoSaver
import com.example.symposium.models.User
import com.example.symposium.utils.BaseActivity
import com.example.symposium.utils.Constants
import timber.log.Timber


class AccountActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityAccountBinding
    private lateinit var mUser: User
    private val photoSaver = PhotoSaver(this, this, this)


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAccountBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Timber.plant(Timber.DebugTree())

        setToolbar()

        initializeUserDataUpload(this)

        binding.clEmail.setOnClickListener(this)
        binding.clPhone.setOnClickListener(this)
        binding.ivEditName.setOnClickListener(this)
        binding.ivAvatar.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.clEmail -> {
                val intent = Intent(this, ChangeUsersDataActivity::class.java)
                intent.putExtra("DataType", Constants.EMAIL)
                userDataChangeResultLauncher.launch(intent)
            }
            R.id.clPhone -> {
                val intent = Intent(this, ChangeUsersDataActivity::class.java)
                intent.putExtra("DataType", Constants.PHONE)
                userDataChangeResultLauncher.launch(intent)
            }
            R.id.ivEditName -> {
                val intent = Intent(this, ChangeUsersDataActivity::class.java)
                intent.putExtra("DataType", Constants.NAME)
                userDataChangeResultLauncher.launch(intent)
            }
            R.id.ivAvatar -> {
                buildPhotoResourceDialog()
            }
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
                firestoreHandler.logOut(this, this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    fun uploadUserDetails(user: User) {
        mUser = user

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

    private fun updateUserDetails() {
        Glide
            .with(this)
            .load(mUser.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding.ivAvatar)
        binding.tvAccountUserName.text = mUser.name
        binding.tvEmail.text = mUser.name
        binding.tvPhone.text = checkNumber(mUser.mobile.toString())
    }

    private val userDataChangeResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                firestoreHandler.getUserData(this)
            }
        }


    private fun setToolbar() {
        setSupportActionBar(binding.toolbarAccount)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarAccount.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun buildPhotoResourceDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf(
            "Select photo from Gallery",
            "Capture photo from Camera"
        )
        pictureDialog.setItems(pictureDialogItems) { _, which ->
            when (which) {
                0 -> {
                    photoSaver.checkGalleryPermissions()
                    updateUserDetails()
                }
                1 -> {
                    photoSaver.checkCameraPermissions()
                    updateUserDetails()
                }
            }
        }
        pictureDialog.show()
    }

}