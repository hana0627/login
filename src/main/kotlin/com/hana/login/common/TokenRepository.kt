package com.hana.login.common

import com.hana.login.common.domain.Token
import org.springframework.data.jpa.repository.JpaRepository

interface TokenRepository : JpaRepository<Token, Long>  {
}