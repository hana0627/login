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
2024. 04. 25 ~ 2024. 05. 22

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
* [MySQL Aceessdenied for user 문제](https://velog.io/@hana0627/Ubuntu-Docker%EC%97%90%EC%84%9C-MySQL-%EC%82%AC%EC%9A%A9%EC%8B%9C-Access-denied-for-user-rootlocalhost)
* (jwt 인증시 db호출 없애기, redis를 활용한 refresh토큰 관리, oauth2 연결, ssl 인증서연결, jenkins 빌드실패 문제, 프리티어 메모리 문제
  등의 문제를 만났으나, 따로 포스팅으로 남기진 않았습니다.
  <br/>
  <br/>


# 프로젝트 후기
---
작성예정입니다.
