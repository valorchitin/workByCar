package com.example.workbycar.ui.view_models

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workbycar.domain.model.UserLogged
import com.example.workbycar.domain.repository.AuthRepository
import com.example.workbycar.utils.CallBackHandle
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private val authRepository: AuthRepository): ViewModel(){
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var name by mutableStateOf("")
    var surname by mutableStateOf("")
    var birthDate by mutableStateOf<Long?>(null)
    var prefix by mutableStateOf("")
    var phone by mutableStateOf("")
    var currentUser by mutableStateOf<UserLogged?>(null)

    fun signUp(authResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            authRepository.createUserWithEmailAndPassword(email,
                password,
                name,
                surname,
                birthDate,
                "",
                CallBackHandle(
                    onSuccess = { authResult.invoke(it) },
                    onError = {
                        authResult(false)
                        Log.e("SignUpViewModel", "Error al crear usuario: $it")
                    }
                ))
        }
    }

    fun addPhone(phone: String, prefix: String, authResult: (Boolean) -> Unit){
        viewModelScope.launch {
            currentUser?.let { user ->
                FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .document(user.uid)
                    .update(
                        "phone", (prefix + phone)
                    )
                    .addOnSuccessListener {
                        authResult(true)
                    }
                    .addOnFailureListener{
                        authResult(false)
                    }

            }
        }
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            authRepository.getCurrentUser(CallBackHandle(
                onSuccess = {user ->
                    currentUser = user
                },
                onError = {
                    Log.e("SignUpViewModel", "Error al obtener el usario actual.")
                }
            ))
        }
    }

    fun signUpScreenInputValid(): Boolean{
        return email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty() && surname.isNotEmpty()
    }
}