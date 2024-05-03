package com.hana.login.common.repositroy

import com.hana.login.common.domain.Token
import org.springframework.data.jpa.repository.JpaRepository

interface TokenRepository : JpaRepository<Token, String>  {
}
