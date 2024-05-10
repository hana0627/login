package com.hana.login.user.repository

import com.hana.login.user.domain.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findByUserId(userId: String): UserEntity?

}
