package com.example.workbycar.data

import com.example.workbycar.domain.model.UserLogged
import com.example.workbycar.domain.repository.AuthRepository
import com.example.workbycar.utils.CallBackHandle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.Year
import javax.inject.Inject

class AuthFirebaseImpl @Inject constructor(private val firebaseAuth: FirebaseAuth) : AuthRepository {
    override fun getCurrentUser(callBack: CallBackHandle<UserLogged>) {
        try {
            val currentUser = firebaseAuth.currentUser

            FirebaseFirestore.getInstance().collection("usuarios")
                .document(currentUser!!.uid)
                .get()
                .addOnSuccessListener { user ->
                    val name = user.getString("name") ?: ""
                    val surname = user.getString("surname") ?: ""

                    val userLogged = UserLogged(
                        uid = currentUser.uid,
                        email = currentUser.email.toString(),
                        name = name,
                        surname = surname,
                    )
                    callBack.onSuccess.invoke(userLogged)
                }
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
        name: String,
        surname: String,
        callBack: CallBackHandle<Boolean>
    ) {
        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{ task ->
                if(task.isSuccessful) {
                    val user_id = firebaseAuth.currentUser?.uid
                    if(user_id != null){
                        val user = hashMapOf(
                            "name" to name,
                            "surname" to surname
                        )

                        FirebaseFirestore.getInstance()
                            .collection("usuarios")
                            .document(user_id)
                            .set(user)
                            .addOnSuccessListener {
                                callBack.onSuccess.invoke(true)
                            }
                    } else {
                        callBack.onError.invoke(null)
                    }
                } else {
                    callBack.onError.invoke(null)
                }
            }
        } catch (e: Exception){
            callBack.onError.invoke(null)
        }
    }
}