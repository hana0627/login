import axios from 'axios';
import { useNavigate } from 'react-router-dom'
import React, { useState } from 'react'
import Swal from "sweetalert2";

function LoginPage() {

    const [memberId, setMemberId] = useState('')
    const [password, setPassword] = useState('')
    const navigate = useNavigate()

    function memberIdHandle(e) {
        setMemberId(e.target.value)
    }
    function passwordHandle(e) {
        setPassword(e.target.value)
    }


    function loginBtnClick() {
        if(memberId === '' ) {
            Swal.fire({
                title: '실패',
                html: '아이디를 입력해주세요.',
                icon: 'warning',
                confirmButtonText: '확인'
            });
            return false;
        }
        if(password === '' ) {
            Swal.fire({
                title: '실패',
                html: '비밀번호를 입력해주세요.',
                icon: 'warning',
                confirmButtonText: '확인'
            });
            return false;
        }


        axios.post('http://localhost:8080/api/v1/login',
            {
                "memberId": memberId,
                "password": password
            },
            {withCredentials: true}
        ).then(response => {
            localStorage.setItem('accessToken',response.data);
            navigate('/MyPage')
        }).catch(error => {
            const data = error.response.data
            if(data.getCode === 'INTERNAL_SERVER_ERROR') {
                Swal.fire({
                    title: '실패',
                    html: '예상하지 못한 에러가 발생했습니다.<br>다시 시도해주세요',
                    icon: 'warning',
                    confirmButtonText: '확인'
                });
            }
            else {
                Swal.fire({
                    title: '실패',
                    html: data.getMessage,
                    icon: 'warning',
                    confirmButtonText: '확인'
                });
            }
        });
    }

    // function googleLogin() {
    //     axios.get("http://localhost:8080/oauth2/authorization/google", {withCredentials: true})
    // }

    function naverLogin() {
        alert('구현중입니다.')
    }

    function kakaoLogin() {
        alert('구현중입니다.')
    }
    function SignupBtnClick () {
        navigate('/SignupPage')
    }


    return (
        <div className="login-page">
            <div className="login-wrapper">
                <h2>Login</h2>
                <input type="text" className="login-input" placeholder="id" value={memberId}
                       onChange={(e) => memberIdHandle(e)}/>
                <a href="http://localhost:8080/oauth2/authorization/google">
                    <img src="/img/google_login.jpg" className="btn_login" alt="구글로그인"/>
                </a>
                <img src="/img/naver_login.jpg" className="btn_login" onClick={naverLogin} alt="네이버로그인"/>
                <img src="/img/kakao_login.jpg" className="btn_login" onClick={kakaoLogin} alt="카카오로그인"/>
                <button className="btn_purple btn_login" onClick={loginBtnClick}>로그인</button>
                <br/><br/>
                <div className="ft-s right" onClick={SignupBtnClick}>아직 회원이 아니신가요?</div>

            </div>
        </div>
    )
}

export default LoginPage;
