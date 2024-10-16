package com.example.workbycar.data

import com.example.workbycar.domain.model.UserLogged
import com.example.workbycar.domain.repository.AuthRepository
import com.example.workbycar.utils.CallBackHandle
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class AuthFirebaseImpl @Inject constructor(private val firebaseAuth: FirebaseAuth) : AuthRepository {
    override fun getCurrentUser(callBack: CallBackHandle<UserLogged>) {
        try {
            val currentUser = firebaseAuth.currentUser
            val userLogged = UserLogged(
                uid = currentUser!!.uid,
                email = currentUser.email.toString(),
                name = "",
                age = ""
            )
            callBack.onSuccess.invoke(userLogged)
        } catch (e: Exception) {
            callBack.onError.invoke(null)
        }
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
        callBack: CallBackHandle<Boolean>
    ) {
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                callBack.onSuccess.invoke(it.isSuccessful)
            }
        } catch (e: Exception) {
            callBack.onError.invoke(null)
        }
    }

    override fun logout(callBack: CallBackHandle<Boolean>) {
        try {
            firebaseAuth.signOut()
            callBack.onSuccess(true)
        } catch (e: Exception) {
            callBack.onError.invoke(null)
        }
    }

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        callBack: CallBackHandle<Boolean>
    ) {
        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{ task ->
                if(task.isSuccessful) {
                    callBack.onSuccess.invoke(true)
                } else {
                    callBack.onError.invoke(null)
                }
            }
        } catch (e: Exception){
            callBack.onError.invoke(null)
        }
    }
}