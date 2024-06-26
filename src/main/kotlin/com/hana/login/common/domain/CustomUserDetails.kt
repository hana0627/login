package com.hana.login.common.domain

import com.hana.login.user.domain.UserEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User

class CustomUserDetails(
    private val userEntity: UserEntity,

    private val attributes: MutableMap<String, Any>,
) : UserDetails, OAuth2User {

    // oauth2
    override fun getName(): String {
        return userEntity.userId
    }

    // oauth2
    override fun getAttributes(): MutableMap<String, Any> {
        return attributes
    }


    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf()
    }

    override fun getPassword(): String {
        return userEntity.password
    }

    override fun getUsername(): String {
        return userEntity.userId
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    fun getUserName(): String {
        return userEntity.userName
    }

    fun getPhoneNumber(): String {
        return userEntity.phoneNumber
    }
}
