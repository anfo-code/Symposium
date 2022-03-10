package com.example.symposium.firebase

import android.app.Activity
import android.util.Log
import com.example.symposium.activities.MainActivity
import com.example.symposium.activities.SignInActivity
import com.example.symposium.activities.SignUpActivity
import com.example.symposium.models.User
import com.example.symposium.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreHandler {

    private val fireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User) {
        fireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
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
                Log.e("Sign In User", "Error writing document")
            }
    }

    fun getUserData(activity: Activity){
        fireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                 val loggedInUser = document.toObject(User::class.java)!!
                when (activity) {
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser)
                    }
                }
            }
            .addOnFailureListener{
                Log.e("GET USER DATA", "Error getting user data")
            }
    }


    private fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }
}