package com.example.workbycar.ui.view_models

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workbycar.domain.repository.AuthRepository
import com.example.workbycar.utils.CallBackHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private val authRepository: AuthRepository): ViewModel(){
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    fun signUp(authResult: (Boolean) -> Unit){
        viewModelScope.launch {
            authRepository.createUserWithEmailAndPassword(email, password, CallBackHandle(
                onSuccess = { authResult.invoke(it) },
                onError = {
                    authResult(false)
                    Log.e("SignUpViewModel", "Error al crear usuario: $it")
                }
            ))
        }
    }
}