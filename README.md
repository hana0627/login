# 🍀 login
### jwt, oauth2를 이용한 로그인 템플릿 제작하기
![bandicam2024-05-2418-09-49-919-ezgif com-video-to-gif-converter](https://github.com/hana0627/login/assets/108846134/ded925bf-4d0e-4306-ac40-f952ab836217)

<br/>
<br/>

#  💻  데모페이지
https://hanaworlds.net
### [PC 화면 권장]
### 프리티어 종료시 혹은 인스턴스 비용 등의 문제로 서버가 종료될 수 있습니다.

# 🪛 개발 환경
* React
* KOTLIN 
* SpringBoot, JPA, QueryDsl
* SpringSecurity, JWT, OAuth2, RestAPI, RestDocs,
* DB - Redis, MySql
* INFRA - dcoker, Jenkis, AWS EC2
<br/>
<br/>


# ⭐ 개발기간
2024.&nbsp;04.&nbsp;25 ~ 2024.&nbsp;05.&nbsp;22

<br/>
<br/>

# 📖 시스템 아키텍쳐
![Architecture Diagram](https://github.com/hana0627/login/assets/108846134/ae3d8e02-b85c-469c-a3c3-1e8a31303764)

<br/>
<br/>
# 📖 시퀀스 다이어그램 

### Oauth2 Login
![Oauth2 Login](https://github.com/hana0627/login/assets/108846134/3a8ef967-6256-484f-b53c-a74187237b99)

### 이메일 로그인
![Login-사본](https://github.com/hana0627/login/assets/108846134/0df3380a-3fc0-46bb-86f5-ed61c096c835)



# 주요기능 및 특징
*  ### 로그인, 소셜로그인(구글, 네이버, 카카오), 로그아웃
    * [React-SpringBoot 소셜로그인 적용하기](https://velog.io/@hana0627/Kotlin-JWT-%ED%86%A0%ED%81%B0-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0-4)
* ### AcessToken을 Redis에 저장
  * refresh 토큰 갱신시 DB조회 최소화
  * 토큰 탈취시 Redis 캐시값을 제거하여 refresh토큰 발급이 불가능하게 기능구축
* ### JWT토큰활용하기
  * 유효한 jwt토큰을 요청에 담을경우 **DB호출 없이** 인증 및 인가절차 수행
    
  <br/>
  <br/>
  
# Trobule Shooting
* [SpringBoot통합테스트 -> Mockito를 이용한 단위테스로 변경](https://velog.io/@hana0627/SpringBootTest-%ED%86%B5%ED%95%A9%ED%85%8C%EC%8A%A4%ED%8A%B8-Mockito%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-%EB%8B%A8%EC%9C%84%ED%85%8C%EC%8A%A4%ED%8A%B8%EB%A1%9C-%EB%B3%80%EA%B2%BD%ED%95%98%EA%B8%B0)
* (jwt 인증시 db호출 없애기, redis를 활용한 refresh토큰 관리, oauth2 연결, ssl 인증서연결, jenkins 빌드실패 문제, ec2 프리티어 메모리 문제
  등의 문제를 만났으나, 따로 포스팅으로 남기진 않았습니다.
  <br/>
  <br/>


---

# Review

* 단위테스트 시, SpringBootTest 어노테이션 제거 및 리팩토링을 통한 테스트 속도 5배 향상
  <br/>
@SpringBootTest 어노테이션 제거를 통해 테스트 커버리지를 동일하게 유지하면서 테스트 실행 속도를 약 **5배 향상**시킬 수 있었습니다.<br/>
규모가 큰 애플리케이션에서는 **단위 테스트가 선택이 아닌 필수**임을 경험했습니다.<br/>
하지만 Mockito를 이용한 단위 테스트가 항상 @SpringBootTest를 이용한 통합 테스트보다 우월한 것은 아님을 인지하게 되었습니다.<br/>
이번 프로젝트의 경우, Mockito를 활용한 단위 테스트로 충분히 검증 가능한 메서드를 통합 테스트로 작성했던 상황이였습니다.<br/>
따라서 모든 테스트를 단위 테스트로 변경하여 실행 속도 향상 효과를 얻을 수 있었습니다.<br/>
하지만 QueryDSL과 같이 외부 리소스 호출이 필요한 코드는 단위 테스트만으로 검증하기 어렵다는 점을 확인했습니다.<br/>
단위 테스트가 적합한 상황에서 통합 테스트를 사용하는 것은 비효율적일 수 있으며, 그렇다고 단순히 **‘단위 테스트가 더 빠르니까 좋다’는 생각보다는 상황에 맞는 테스트 전략을 선택하는 것이 중요하다고 느꼈습니다.**<br/>

