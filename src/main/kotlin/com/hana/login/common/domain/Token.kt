package com.hana.login.common.domain

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
class Token (
    @Column(length = 50, updatable = false)
    val memberId: String,
    @Column(updatable = false)
    val expiredAt: Date,
    @Column(length = 200, updatable = false)
    val refreshToken: String,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?
){


}