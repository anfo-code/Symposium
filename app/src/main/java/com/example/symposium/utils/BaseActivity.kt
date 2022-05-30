package com.example.symposium.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.example.symposium.R
import com.example.symposium.firebase.FirestoreHandler
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

open class BaseActivity : AppCompatActivity() {
    private var doubleBackToExitPressedOnce = false
    private lateinit var progressDialog: Dialog
    var firestoreHandler = FirestoreHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        Timber.plant()
    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun setNightMode() {
        val appSettingPrefs: SharedPreferences = getSharedPreferences("AppSettingPrefs", 0)
        val isDayModeOn: Boolean = appSettingPrefs.getBoolean("NightMode", false)

        if (isDayModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            turnOffDarkMode()
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            turnOnDarkMode()
        }
    }

    fun showProgressDialog(text: String) {
        progressDialog = Dialog(this)

        progressDialog.setContentView(R.layout.dialog_progress)

        val tvProgressText = progressDialog.findViewById<TextView>(R.id.tvProgressText)

        tvProgressText.text = text

        progressDialog.setCancelable(false)

        progressDialog.show()
    }

    fun cancelProgressDialog() {
        progressDialog.dismiss()
    }

    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun doubleBackToExit() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        } else {
            this.doubleBackToExitPressedOnce = true
            Toast.makeText(
                this,
                "Please, click back again to exit.",
                Toast.LENGTH_SHORT
            ).show()

            Handler(Looper.myLooper()!!)
                .postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
        }
    }

    fun showErrorSnackBar(message: String) {
        val snackBar = Snackbar
            .make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.snackBarErrorColor))
        snackBar.show()
    }

    fun turnOffDarkMode() {
        val appSettingPrefs: SharedPreferences = getSharedPreferences("AppSettingPrefs", 0)
        val sharedPreferencesEditor: SharedPreferences.Editor = appSettingPrefs.edit()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        sharedPreferencesEditor.putBoolean("NightMode", true)
        sharedPreferencesEditor.apply()
    }

    fun turnOnDarkMode() {
        val appSettingPrefs: SharedPreferences = getSharedPreferences("AppSettingPrefs", 0)
        val sharedPreferencesEditor: SharedPreferences.Editor = appSettingPrefs.edit()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        sharedPreferencesEditor.putBoolean("NightMode", false)
        sharedPreferencesEditor.apply()
    }

    fun checkNumber(mobile: String): String {
        return if (mobile == "0") {
            ""
        } else {
            mobile
        }
    }

    fun initializeUserDataUpload(activity: Activity) {
        firestoreHandler.getUserData(activity)
    }

    fun showRationalDialogForPermissions() {
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
}