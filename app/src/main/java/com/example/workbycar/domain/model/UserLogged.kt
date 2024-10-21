package com.example.workbycar.domain.model

import java.time.LocalDate
import java.util.Date

data class UserLogged(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val surname: String = "",
    val birthDate: LocalDate
)