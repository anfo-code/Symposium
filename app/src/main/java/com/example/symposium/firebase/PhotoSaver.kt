package com.example.symposium.firebase

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.symposium.utils.BaseActivity
import com.example.symposium.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import timber.log.Timber
import java.io.ByteArrayOutputStream
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

                            updateImageDataInStorage(profileImageUrl, activity)

                            fireStoreHandler.getUserData(activity)
                        }
                        .addOnFailureListener { exception ->
                            BaseActivity().showErrorSnackBar(exception.message!!)
                        }

                }
        }
    }

    fun checkGalleryPermissions(activity: BaseActivity) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED
        ) {
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            galleryResultLauncher.launch(galleryIntent)
        } else {
            activity.showToastForPermissions()
        }
    }

    fun checkCameraPermissions(activity: BaseActivity) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            val galleryIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraResultLauncher.launch(galleryIntent)
        } else {
            activity.showToastForPermissions()
        }
    }

    private val galleryResultLauncher =
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

    private val cameraResultLauncher =
        componentActivity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val thumbnail = result.data!!.extras!!.get("data") as Bitmap
                selectedImageFileUri = convertBitmapToUri(thumbnail)
                try {
                    uploadUserImage()
                } catch (e: IOException) {
                    e.printStackTrace()
                    baseActivity.showErrorSnackBar("Failed to upload photo from Gallery")
                }
            }
        }

    private fun convertBitmapToUri(thumbnail: Bitmap): Uri {
        val byteArray = ByteArrayOutputStream()
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, byteArray)
        val path = MediaStore.Images.Media.insertImage(
            context.contentResolver,
            thumbnail,
            "USER_IMAGE" + System.currentTimeMillis(),
            null
        )

        return Uri.parse(path)
    }

    private fun updateImageDataInStorage(imageUrl: String, activity: Activity) {
        val userHashMap = HashMap<String, Any>()

        userHashMap[Constants.IMAGE] = imageUrl

        fireStoreHandler.changeData(activity, userHashMap)

    }
}