package com.hana.login.user.repository

import com.hana.login.user.domain.MemberEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<MemberEntity, Long> {
    fun findByMemberId(memberId: String): MemberEntity?

}
