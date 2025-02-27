package com.example.workbycar.domain.model

data class UserLogged(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val surname: String = "",
    val birthDate: Long?,
    val prefix: String = "",
    val phone: String = "",
){
    constructor() : this("", "", "", "", null, "", "")
}