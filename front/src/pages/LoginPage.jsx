import axios from 'axios';
import React, { useState } from 'react'

function LoginPage() {

    const [memberId, setMemberId] = useState('')
    const [password, setPassword] = useState('')

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
        ).then((response) => {
            console.log(response.data);
        }).catch(() => {
            console.error('에러발생');
        });
    }

    return (
        <>
            <div className="login-wrapper">
                <h2>Login</h2>
                    <input type="text" className = "login-input" placeholder="id" value={memberId} onChange={(e) => memberIdHandle(e)}/>
                    <input type="password" className = "login-input" value={password} placeholder="Password"  onChange={(e) => passwordHandle(e)}/>
                    <button className="btn_purple" onClick={loginBtnClick}>로그인</button>
            </div>
        </>
    )
}

export default LoginPage;
