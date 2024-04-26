package com.hana.login.user.controller

import com.hana.login.user.controller.request.MemberCreate
import com.hana.login.user.controller.request.MemberLogin
import com.hana.login.user.service.MemberService
import lombok.RequiredArgsConstructor
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequiredArgsConstructor
class MemberController (
    private val memberService: MemberService
){

    @PostMapping("/api/v1/member")
    fun saveMember(@RequestBody requestDto: MemberCreate): Long {
        return memberService.join(requestDto);
    }

    @PostMapping("/api/v1/login")
    fun login(@RequestBody requestDto: MemberLogin): Long {
        return memberService.login(requestDto);
    }
}
