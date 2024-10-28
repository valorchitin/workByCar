package com.example.workbycar.domain.repository

import com.example.workbycar.domain.model.UserLogged
import com.example.workbycar.utils.CallBackHandle

interface AuthRepository {

    fun getCurrentUser(callBack: CallBackHandle<UserLogged>)

    suspend fun signInWithEmailAndPassword(email: String, password: String, callBack: CallBackHandle<Boolean>)

    fun logout(callBack: CallBackHandle<Boolean>)

    suspend fun createUserWithEmailAndPassword(email: String, password: String, name: String, surname: String, birthDate: Long?, phone: String, callBack: CallBackHandle<Boolean>)
}