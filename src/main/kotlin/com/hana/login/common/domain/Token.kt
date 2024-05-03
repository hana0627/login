package com.hana.login.common.domain

import jakarta.persistence.*
import java.util.*

@Entity
class Token (
    @Column(length = 50, updatable = false)
    @Id
    val memberId: String,
    @Column(updatable = false)
    val expiredAt: Date,
    @Column(length = 200, updatable = false)
    val refreshToken: String,
){

}
