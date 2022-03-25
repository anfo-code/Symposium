package com.example.symposium.firebase

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.symposium.utils.BaseActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import timber.log.Timber
import java.io.IOException

class PhotoSaver(
    private val context: Context,
    private val activity: Activity,
    componentActivity: ComponentActivity
) {

    private var selectedImageFileUri: Uri? = null
    private var profileImageUrl = ""
    private val fireStoreHandler = FirestoreHandler()
    private val baseActivity = BaseActivity()

    private fun getFileExtension(uri: Uri?): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(context.contentResolver.getType(uri!!))
    }

    @Suppress("Deprecation")
    private fun getImage(contentURI: Uri): Bitmap {
        return when {
            Build.VERSION.SDK_INT < 28 ->
                MediaStore.Images.Media.getBitmap(context.contentResolver, contentURI)
            else -> ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(
                    context.contentResolver,
                    contentURI
                )
            )
        }
    }

    private fun uploadUserImage() {
        if (selectedImageFileUri != null) {

            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "USER_IMAGE" + System.currentTimeMillis() + "." + getFileExtension(
                    selectedImageFileUri
                )
            )

            sRef.putFile(selectedImageFileUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    Timber.e("Firebase Image URI" + taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            Timber.e("Downloadable Image URI$uri")

                            profileImageUrl = uri.toString()

                            fireStoreHandler.getUserData(activity)
                        }
                        .addOnFailureListener { exception ->
                            BaseActivity().showErrorSnackBar(exception.message!!)
                        }

                }
        }
    }

    fun checkGalleryPermissions() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED
        ) {
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            imageTakeResultLauncher.launch(galleryIntent)
        } else {
            baseActivity.showRationalDialogForPermissions()
        }
    }

    fun checkCameraPermissions() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            imageTakeResultLauncher.launch(galleryIntent)
        } else {
            baseActivity.showRationalDialogForPermissions()
        }
    }

    private val imageTakeResultLauncher =
        componentActivity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    selectedImageFileUri = data.data!!
                    try {
                        uploadUserImage()
                    } catch (e: IOException) {
                        e.printStackTrace()
                        baseActivity.showErrorSnackBar("Failed to upload photo from Gallery")
                    }
                }
            }
        }
}