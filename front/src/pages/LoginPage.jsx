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

    function SignupBtnClick () {
        navigate('/SignupPage')
    }


    return (
        <div className="login-page">
            <div className="login-wrapper">
                <h2>Login</h2>
                    <input type="text" className = "login-input" placeholder="id" value={memberId} onChange={(e) => memberIdHandle(e)}/>
                    <input type="password" className = "login-input" value={password} placeholder="Password"  onChange={(e) => passwordHandle(e)}/>
                    <div className="ft-s right" onClick={SignupBtnClick}>회원가입</div>
                    <button className="btn_purple" onClick={loginBtnClick}>로그인</button>
            </div>
        </div>
    )
}

export default LoginPage;
