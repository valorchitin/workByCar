package com.example.workbycar.mocks

import com.example.workbycar.domain.model.UserLogged
import com.example.workbycar.domain.repository.AuthRepository
import com.example.workbycar.utils.CallBackHandle

class MockAuthRepository : AuthRepository {

    var signUpCalled = false
    var getCurrentUserCalled = false

    override fun getCurrentUser(callBack: CallBackHandle<UserLogged>) {
        getCurrentUserCalled = true
        val user = UserLogged(
            uid = "hjePaeSyJQeGBbnthSZzD8Crouy2",
            email = "juanaedo@gmail.com",
            name = "Juan",
            surname = "Martínez aedo",
            birthDate = 986860800000,
            description = "Description about Juan ",
            prefix = "+34",
            phone = "123456789"
        )
        callBack.onSuccess(user)
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
        callBack: CallBackHandle<Boolean>
    ) {
        callBack.onSuccess(true)
    }

    override fun logout(callBack: CallBackHandle<Boolean>) {
        callBack.onSuccess(true)
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
        signUpCalled = true
        callBack.onSuccess(true)
    }
}
