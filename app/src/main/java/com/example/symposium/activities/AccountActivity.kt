package com.example.symposium.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.decodeBitmap
import com.bumptech.glide.Glide
import com.example.symposium.BuildConfig
import com.example.symposium.R
import com.example.symposium.databinding.ActivityAccountBinding
import com.example.symposium.models.User
import com.example.symposium.utils.BaseActivity
import com.example.symposium.utils.Constants
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Releasable
import com.google.firebase.auth.FirebaseAuth
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.Exception
import java.util.*


class AccountActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityAccountBinding
    private var saveImageToInternalStorage: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAccountBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Timber.plant(Timber.DebugTree())


        setToolbar()
        firestoreHandler.getUserData(this)

        binding.clEmail.setOnClickListener(this)
        binding.clPhone.setOnClickListener(this)
        binding.ivEditName.setOnClickListener(this)
        binding.ivAvatar.setOnClickListener(this)
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
            R.id.clEmail -> {
                val intent = Intent(this, ChangeUsersDataActivity::class.java)
                intent.putExtra("DataType", Constants.EMAIL)
                resultLauncher.launch(intent)
            }
            R.id.clPhone -> {
                val intent = Intent(this, ChangeUsersDataActivity::class.java)
                intent.putExtra("DataType", Constants.PHONE)
                resultLauncher.launch(intent)
            }
            R.id.ivEditName -> {
                val intent = Intent(this, ChangeUsersDataActivity::class.java)
                intent.putExtra("DataType", Constants.NAME)
                resultLauncher.launch(intent)
            }
            R.id.ivAvatar -> {
                buildPhotoResourceDialog()
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

    private fun buildPhotoResourceDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf(
            "Select photo from Gallery",
            "Capture photo from Camera"
        )
        pictureDialog.setItems(pictureDialogItems) { _, which ->
            when (which) {
                0 -> selectPhotoFromGallery()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    private fun selectPhotoFromGallery() {
        Dexter.withActivity(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()) {
                    val galleryIntent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    galleryResultLauncher.launch(galleryIntent)

                }
            }
            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
                showRationalDialogForPermissions()
            }

        }).onSameThread().check()
    }

    private fun takePhotoFromCamera() {

    }

    val galleryResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val contentURI = data.data
                    try {
                        val selectedBitmap: Bitmap = getImage(contentURI!!)

                        saveImageToInternalStorage = saveImageToInternalStorage(selectedBitmap)

                        binding.ivAvatar.setImageBitmap(selectedBitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        showErrorSnackBar("Failed to upload photo from Gallery")
                    }
                }
            }
        }

    private fun getImage(contentURI: Uri): Bitmap {
        return when {
            Build.VERSION.SDK_INT < 28 ->
                MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
            else -> ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(
                    this.contentResolver,
                    contentURI
                )
            )
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this).setMessage(
            "It looks like you have turned off permissions required for this feature. " +
                    "It can be enabled under Application Settings"
        )
            .setPositiveButton("GO TO SETTINGS")
            { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
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

    companion object {
        private const val IMAGE_DIRECTORY = "SymposiumImages"
    }
}