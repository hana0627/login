package com.hana.login.user.service

import com.hana.login.common.exception.ApplicationException
import com.hana.login.common.exception.en.ErrorCode
import com.hana.login.user.controller.request.MemberCreate
import com.hana.login.user.controller.request.MemberLogin
import com.hana.login.user.domain.MemberEntity
import com.hana.login.user.repository.MemberRepository
import lombok.RequiredArgsConstructor
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@RequiredArgsConstructor
class MemberService (
    private val memberRepository: MemberRepository,
    private val passwordEncoder: BCryptPasswordEncoder
){
    @Transactional
    fun join(dto : MemberCreate): Long {
        if (memberRepository.findByMemberId(dto.memberId) != null) {
            throw ApplicationException(ErrorCode.DUPLICATED_MEMBER_ID,"이미 가입된 회원입니다.")
        }
        val member: MemberEntity =  MemberEntity.of(dto);
        return memberRepository.save(member).id!!
    }

    fun login(dto: MemberLogin): Long {
        val member: MemberEntity = memberRepository.findByMemberId(dto.memberId)
            ?: throw ApplicationException(ErrorCode.MEMBER_NOT_FOUNT, "회원 정보가 없습니다.")

        if(!passwordEncoder.matches(dto.password,member.password)) {
            throw ApplicationException(ErrorCode.MEMBER_NOT_FOUNT, "회원 정보가 없습니다.")
        }
        return member.id!!;
    }
}
