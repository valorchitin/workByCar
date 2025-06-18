package com.example.workbycar.data

import com.example.workbycar.domain.model.UserLogged
import com.example.workbycar.domain.repository.AuthRepository
import com.example.workbycar.utils.CallBackHandle
import com.example.workbycar.utils.ErrorData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
                    val birthDate = user.getLong("birthDate")
                    val description = user.getString("description") ?: ""
                    val prefix = user.getString("prefix") ?: ""
                    val phone = user.getString("phone") ?: ""

                    val userLogged = UserLogged(
                        uid = currentUser.uid,
                        email = currentUser.email.toString(),
                        name = name,
                        surname = surname,
                        birthDate = birthDate,
                        description = description,
                        prefix = prefix,
                        phone = phone,
                    )
                    callBack.onSuccess.invoke(userLogged)
                }
        } catch (e: Exception) {
            callBack.onError.invoke(ErrorData(e))
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
            callBack.onError.invoke(ErrorData(e))
        }
    }

    override fun logout(callBack: CallBackHandle<Boolean>) {
        try {
            firebaseAuth.signOut()
            callBack.onSuccess(true)
        } catch (e: Exception) {
            callBack.onError.invoke(ErrorData(e))
        }
    }

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        name: String,
        surname: String,
        birthDate: Long?,
        description: String,
        prefix: String,
        phone: String,
        callBack: CallBackHandle<Boolean>
    ) {
        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{ task ->
                if(task.isSuccessful) {
                    val user_id = firebaseAuth.currentUser?.uid
                    if(user_id != null){
                        val user = hashMapOf(
                            "name" to name,
                            "surname" to surname,
                            "birthDate" to birthDate,
                            "description" to description,
                            "prefix" to prefix,
                            "phone" to phone
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
            callBack.onError.invoke(ErrorData(e))
        }
    }
}