import axios from 'axios';
import {useLocation, useNavigate} from 'react-router-dom'
import React, {useEffect, useState} from 'react'
import Swal from "sweetalert2";

function LoginPage() {

    const base_url = process.env.REACT_APP_API_URL
    const [userId, setUserId] = useState('')
    const [password, setPassword] = useState('')
    const navigate = useNavigate()

    const location = useLocation();


    useEffect(() => {
        // URL에서 토큰 추출
        const urlParams = new URLSearchParams(location.search);
        const token = urlParams.get('token');
        // 토큰이 존재하면 저장
        if(token) {
            localStorage.setItem('accessToken',token);
            navigate('/MyPage')
        }

    }, [location]);


    function userIdHandle(e) {
        setUserId(e.target.value)
    }
    function passwordHandle(e) {
        setPassword(e.target.value)
    }


    function loginBtnClick() {
        if(userId === '' ) {
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


        axios.post(base_url+'/api/v1/login',
            {
                "userId": userId,
                "password": password
            },
            {withCredentials: true}
        ).then(response => {
            localStorage.setItem('accessToken',response.data.result);
            navigate('/MyPage')
        }).catch(error => {
            const data = error.response.data.error
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

    function SignupBtnClick () {
        navigate('/SignupPage')
    }


    return (
        <div className="login-page">
            <div className="login-wrapper">
                <h2>Login</h2>
                <input type="text" className="login-input" placeholder="id" value={userId}
                       onChange={(e) => userIdHandle(e)}/>
                <input type="password" className="login-input" placeholder="password" value={password}
                       onChange={(e) => passwordHandle(e)}/>
                <a href={base_url+"/oauth2/authorization/google"}>
                    <img src="/img/google_login.jpg" className="btn_login" alt="구글로그인"/>
                </a>
                <a href={base_url+"/oauth2/authorization/naver"}>
                    <img src="/img/naver_login.jpg" className="btn_login" alt="네이버로그인"/>
                </a>

                <a href={base_url+"/oauth2/authorization/kakao"}>
                    <img src="/img/kakao_login.jpg" className="btn_login" alt="카카오로그인"/>
                </a>
                <button className="btn_purple btn_login" onClick={loginBtnClick}>로그인</button>
                <br/><br/>
                <div className="ft-s right" onClick={SignupBtnClick}>아직 회원이 아니신가요?</div>

            </div>
        </div>
    )
}

export default LoginPage;
