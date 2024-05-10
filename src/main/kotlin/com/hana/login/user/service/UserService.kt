package com.hana.login.user.service

import com.hana.login.common.exception.ApplicationException
import com.hana.login.common.exception.en.ErrorCode
import com.hana.login.user.controller.request.UserCreate
import com.hana.login.user.controller.request.UserLogin
import com.hana.login.user.controller.response.UserInformation
import com.hana.login.user.domain.UserEntity
import com.hana.login.user.repository.UserCacheRepository
import com.hana.login.user.repository.UserRepository
import lombok.RequiredArgsConstructor
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@RequiredArgsConstructor
class UserService(
    private val userCacheRepository: UserCacheRepository,
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder,
) {

    @Transactional
    fun join(dto: UserCreate): Long {
        if (duplicateUser(dto.userId)) {
            val user: UserEntity = UserEntity(
                userId = dto.userId,
                userName = dto.userName,
                password = passwordEncoder.encode(dto.password),
                phoneNumber = dto.phoneNumber,
                gender = dto.gender,
                id = null
            )
            return userRepository.save(user).id!!
        }
        throw ApplicationException(ErrorCode.USER_NOT_FOUNT, "회원 정보가 없습니다.")
    }


    fun login(dto: UserLogin): UserEntity {

        val user:UserEntity = userCacheRepository.getUser(dto.userId) ?:
            getUserByUserIdOrException(dto.userId)
        if (!passwordEncoder.matches(dto.password, user.password)) {
            throw ApplicationException(ErrorCode.USER_NOT_FOUNT, "회원 정보가 없습니다.")
        }

        return user
    }

    fun duplicateUser(userId: String): Boolean {
        if (userRepository.findByUserId(userId) != null) {
            throw ApplicationException(ErrorCode.DUPLICATED_USER_ID, "이미 가입된 회원입니다.")
        }
        return true;
    }


    private fun getUserByUserIdOrException(userId: String): UserEntity {
        return userRepository.findByUserId(userId)
            ?: throw ApplicationException(ErrorCode.USER_NOT_FOUNT, "회원 정보가 없습니다.")
    }

    fun getUserSimpleInformation(userId: String): UserInformation {
        val user: UserEntity = userCacheRepository.getUser(userId)?:
            getUserByUserIdOrException(userId)
        return UserInformation(userId = user.userId, userName = user.userName, phoneNumber = user.phoneNumber)
    }
}
