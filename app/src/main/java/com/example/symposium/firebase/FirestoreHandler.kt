package com.example.symposium.firebase

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.symposium.activities.*
import com.example.symposium.models.User
import com.example.symposium.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import timber.log.Timber

class FirestoreHandler {

    private val fireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User) {
        fireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }
            .addOnFailureListener {
                activity.userRegisteredFailure(it.localizedMessage!!)
            }
    }

    fun signInUser(activity: SignInActivity) {
        fireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener {
                activity.signInSuccess()
            }
            .addOnFailureListener {
                it.localizedMessage?.let { it1 -> activity.signInFailure(it1) }
            }
    }

    fun logOut(activity: Activity, context: Context) {
        //Firebase Log Out
        FirebaseAuth.getInstance().signOut()
        activity.finish()
        startActivity(context, Intent(context, LoginActivity::class.java), null)
    }

    //This Function can't return a user, because success listener is not able to return ANYTHING
    fun getUserData(activity: Activity) {
        fireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)!!
                when (activity) {
                    is MainActivity -> {
                        activity.uploadNavigationUserDetails(loggedInUser)
                    }
                    is AccountActivity -> {
                        activity.uploadUserDetails(loggedInUser)
                    }
                    is ChangeUsersDataActivity -> {
                        activity.setCurrentDataInEditText(loggedInUser)

                    }
                }
            }
            .addOnFailureListener {
                Timber.e("Error getting user data " + it.printStackTrace())
            }
    }

    fun changeData(activity: Activity, userHaspMap: HashMap<String, Any>) {
        fireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHaspMap)
            .addOnSuccessListener {
                Timber.i("Profile updated successfully")
                Toast.makeText(
                    activity, "Profile updated successfully",
                    Toast.LENGTH_SHORT
                ).show()
                onUpdateSuccessListener(activity)
            }
            .addOnFailureListener {
                Timber.e(it.printStackTrace().toString())
            }
    }

    fun onUpdateSuccessListener(activity: Activity) {
        activity.setResult(Activity.RESULT_OK)
        activity.finish()
    }

    private fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }
}