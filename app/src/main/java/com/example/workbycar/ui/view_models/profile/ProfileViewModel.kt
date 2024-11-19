package com.example.workbycar.ui.view_models.profile

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.workbycar.domain.model.UserLogged
import com.example.workbycar.domain.repository.AuthRepository
import com.example.workbycar.ui.navigation.AppScreens
import com.example.workbycar.utils.CallBackHandle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class ProfileViewModel @Inject constructor(private val authRepository: AuthRepository): ViewModel(){
    var currentUser by mutableStateOf<UserLogged?>(null)
    var name by mutableStateOf("")
    var surname by mutableStateOf("")
    var birthDate by mutableStateOf<Long?>(null)
    var prefix by mutableStateOf("")
    var phone by mutableStateOf("")

    init {
        userInfo()
    }

    fun userInfo(){
        viewModelScope.launch {
            authRepository.getCurrentUser(CallBackHandle(
                onSuccess = { user ->
                    currentUser = user
                    name = user.name
                    surname = user.surname
                    birthDate = user.birthDate
                    prefix = user.prefix
                    phone = user.phone
                },
                onError = {
                    Log.e("ProfileViewModel", "Error al acceder a la informacion del usuario: $it")
                }
            ))
        }
    }

    fun editUserInfo(newName: String, newSurname: String, newBirthDate: Long?, newPrefix: String, newPhone: String){
        viewModelScope.launch {
            currentUser?.let { user ->
                FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .document(user.uid)
                    .update(
                        "name", (newName),
                        "surname", (newSurname),
                        "birthDate", (newBirthDate),
                        "prefix", (newPrefix),
                        "phone", (newPhone)
                    )
            }
        }
    }

    fun logOut(){
        FirebaseAuth.getInstance().signOut()
    }

    fun deleteAccount(navController: NavController, context: Context) {
        val user = FirebaseAuth.getInstance().currentUser
        val firestoredatabase = FirebaseFirestore.getInstance()

        if (user?.uid != null){
            firestoredatabase.collection("usuarios").document(user.uid).delete()
                .addOnCompleteListener{response ->
                    if (response.isSuccessful) {
                        user.delete().addOnCompleteListener { response2 ->
                            if (response2.isSuccessful){
                                Toast.makeText(context, "Account deleted successfully", Toast.LENGTH_LONG).show()
                                navController.navigate(AppScreens.LogInScreen.route){
                                    popUpTo(0)
                                }
                            } else {
                                Toast.makeText(context, "Failed to delete account: ${response2.exception?.message}", Toast.LENGTH_LONG).show()
                            }

                        }
                    } else {
                        Toast.makeText(context, "Failed to delete account: ${response.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}