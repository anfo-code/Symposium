package com.example.symposium.firebase

import com.example.symposium.activities.SignUpActivity
import com.example.symposium.models.User
import com.example.symposium.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreHandler() {

    private val FireStore = FirebaseFirestore.getInstance()

     fun registerUser(activity: SignUpActivity, userInfo: User) {
         FireStore.collection(Constants.USERS)
             .document(getCurrentUserId())
             .set(userInfo, SetOptions.merge())
             .addOnSuccessListener {
                 activity.userRegisteredSuccess()
             }
     }

    fun getCurrentUserId(): String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }
}