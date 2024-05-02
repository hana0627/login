package com.hana.login.user.service

import com.hana.login.common.exception.ApplicationException
import com.hana.login.common.exception.en.ErrorCode
import com.hana.login.common.utils.JwtUtils
import com.hana.login.user.controller.request.MemberCreate
import com.hana.login.user.controller.request.MemberLogin
import com.hana.login.user.domain.MemberEntity
import com.hana.login.user.repository.MemberRepository
import jakarta.servlet.http.HttpServletResponse
import lombok.RequiredArgsConstructor
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@RequiredArgsConstructor
class MemberService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val jwtUtils: JwtUtils
) {


    @Transactional
    fun join(dto: MemberCreate): Long {
        if (duplicateMember(dto.memberId)) {
            val member: MemberEntity = MemberEntity(
                memberId = dto.memberId,
                memberName = dto.memberName,
                password = passwordEncoder.encode(dto.password),
                phoneNumber = dto.phoneNumber,
                gender = dto.gender,
                id = null
            )
            return memberRepository.save(member).id!!
        }
        throw ApplicationException(ErrorCode.MEMBER_NOT_FOUNT, "회원 정보가 없습니다.")
    }


    fun login(dto: MemberLogin, response: HttpServletResponse): String {
        val member: MemberEntity = memberRepository.findByMemberId(dto.memberId)
            ?: throw ApplicationException(ErrorCode.MEMBER_NOT_FOUNT, "회원 정보가 없습니다.")

        if (!passwordEncoder.matches(dto.password, member.password)) {
            throw ApplicationException(ErrorCode.MEMBER_NOT_FOUNT, "회원 정보가 없습니다.")
        }
        // 토큰생성
        return jwtUtils.generateToken(
            response = response,
            memberId = member.memberId,
            memberName = member.memberName
        )
    }

    fun duplicateMember(memberId: String): Boolean {
        if (memberRepository.findByMemberId(memberId) != null) {
            throw ApplicationException(ErrorCode.DUPLICATED_MEMBER_ID, "이미 가입된 회원입니다.")
        }
        return true;
    }
}
