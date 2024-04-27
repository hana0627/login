import React, {useState} from 'react'

function SignupPage() {


    return (
        <div className="signup-page">
            <div className="signup-wrapper">
                <h2>회원가입을 위해<br/>정보를 입력해주세요.</h2><br/><br/>
                <span className="ft-lightgrey">* 아이디</span><br/><br/>
                <input className="input"/>

                <span className="ft-lightgrey">*이름</span><br/><br/>
                <input className="input"/>

                <span className="ft-lightgrey">* 비밀번호</span><br/><br/>
                <input className="input" type="password"/>

                <span className="ft-lightgrey">* 비밀번호 확인</span><br/><br/>
                <input className="input" type="password"/>

                <span className="ft-lightgrey">* 전화번호</span><br/><br/>
                <input className="input"/>
                <div style={{textAlign:'left'}}>
                    <input type="radio" className="radio" name="gender"/><span className="ft-bold" >남성</span>
                    <input type="radio" className="radio" name="gender"/><span className="ft-bold">여성</span>
                </div>
                <br/><br/>
                <div style={{textAlign:'left'}}>
                    <input type="checkbox" className="agree"/><span className="ft-bold">이용약관 개인정보 수집 및 정보이용에 동의합니다.</span><br/><br/><br/>
                </div>
                <button className="button"><span className="ft-sm ft-bold">가입하기</span></button>
            </div>
        </div>
    )
}

export default SignupPage;
