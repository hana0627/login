package com.hana.login.user.service

import com.hana.login.user.controller.request.MemberCreate
import com.hana.login.user.controller.request.MemberLogin
import com.hana.login.user.domain.MemberEntity
import com.hana.login.user.repository.MemberRepository
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@RequiredArgsConstructor
class MemberService (
    private val memberRepository: MemberRepository
){
    @Transactional
    fun join(dto : MemberCreate): Long {

        if (memberRepository.findByMemberId(dto.memberId) != null) {
            throw IllegalStateException("이미 가입된 회원입니다.")
        }


        val member: MemberEntity =  MemberEntity.of(dto);
        return memberRepository.save(member).id!!
    }

    fun login(dto: MemberLogin): Long {
        val member: MemberEntity = memberRepository.findByMemberId(dto.memberId) ?: throw IllegalStateException("회원가입된회원")

        if(member.password != dto.password) {
            throw IllegalStateException("비밀번호 불일치")
        }
        return member.id!!;
    }


}