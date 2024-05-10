package com.hana.login.user.domain

import com.hana.login.common.domain.AuditingFields
import com.hana.login.common.domain.en.Gender
import jakarta.persistence.*


@Entity
@Table(name = "user_account")
data class UserEntity(
    @Column(length = 100, updatable = false)
    var userId: String,
    @Column(length = 50)
    var userName: String,
    @Column(length = 200)
    var password: String,
    @Column(length = 50)
    var phoneNumber: String,
    @Column(length = 1)
    @Enumerated(EnumType.STRING)
    val gender: Gender,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,
) : AuditingFields() {

    companion object {
        fun fixture(
           userId: String = "hanana0627",
           userName: String = "박하나",
           password: String = "password",
           phoneNumber: String = "01012345678",
           gender: Gender = Gender.F,
           id: Long? = null
        ) : UserEntity {
            return UserEntity(
                userId = userId,
                userName = userName,
                password = password,
                phoneNumber = phoneNumber,
                gender = gender,
                id= id)
        }
    }

}
