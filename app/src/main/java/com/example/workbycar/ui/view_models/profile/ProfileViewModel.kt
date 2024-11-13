package com.example.workbycar.ui.view_models.profile

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workbycar.domain.model.UserLogged
import com.example.workbycar.domain.repository.AuthRepository
import com.example.workbycar.utils.CallBackHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class ProfileViewModel @Inject constructor(private val authRepository: AuthRepository): ViewModel(){
    var currentUser by mutableStateOf(UserLogged("", "", "", "", null, ""))
    var name by mutableStateOf("")
    var surname by mutableStateOf("")
    var birthDate by mutableStateOf<Long?>(null)

    init {
        userInfo()
    }

    fun userInfo(){
        viewModelScope.launch {
            authRepository.getCurrentUser(CallBackHandle(
                onSuccess = { user ->
                    currentUser = user
                    name = currentUser.name
                    surname = currentUser.surname
                    birthDate = currentUser.birthDate
                },
                onError = {
                    Log.e("ProfileViewModel", "Error al acceder a la informacion del usuario: $it")
                }
            ))
        }
    }

}