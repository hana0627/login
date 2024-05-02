package com.hana.login.user.controller

import com.hana.login.user.controller.request.MemberCreate
import com.hana.login.user.controller.request.MemberLogin
import com.hana.login.user.service.MemberService
import jakarta.servlet.http.HttpServletResponse
import lombok.RequiredArgsConstructor
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequiredArgsConstructor
class MemberController (
    private val memberService: MemberService
){
    @GetMapping("/api/v1/duplicate/{memberId}")
    fun duplicateMember(@PathVariable memberId: String): ResponseEntity<Boolean> {
        val result:Boolean = memberService.duplicateMember(memberId);
        return ResponseEntity.ok(result)
    }

    @PostMapping("/api/v1/join")
    fun saveMember(@RequestBody requestDto: MemberCreate): ResponseEntity<Long> {
        val result: Long =  memberService.join(requestDto);

        return ResponseEntity.ok(result)
    }

    @PostMapping("/api/v1/login")
    fun login(@RequestBody requestDto: MemberLogin, response: HttpServletResponse): ResponseEntity<String> {
        val result:String = memberService.login(requestDto, response)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/api/v2/auth")
    fun MyPage(): ResponseEntity<Any> {
        return ResponseEntity.ok("성공!")
    }
}
