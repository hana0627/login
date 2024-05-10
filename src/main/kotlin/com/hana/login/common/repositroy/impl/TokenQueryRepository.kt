package com.hana.login.common.repositroy.impl

import com.hana.login.common.domain.QToken
import com.hana.login.common.domain.QToken.token
import com.hana.login.common.domain.Token
import com.querydsl.jpa.impl.JPAQueryFactory
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@RequiredArgsConstructor
class TokenQueryRepository(
    private val queryFactory: JPAQueryFactory,
) {
    @Transactional
    fun update(entity: Token) {
        queryFactory.update(token)
            .set(token.expiredAt, entity.expiredAt)
            .set(token.refreshToken, entity.refreshToken)
            .where(token.userId.eq(entity.userId)).execute()
    }
}
