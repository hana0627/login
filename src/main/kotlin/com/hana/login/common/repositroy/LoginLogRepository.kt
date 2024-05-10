package com.hana.login.common.repositroy

import com.hana.login.common.domain.LoginLog
import org.springframework.data.jpa.repository.JpaRepository

interface LoginLogRepository : JpaRepository<LoginLog, Long> {
}