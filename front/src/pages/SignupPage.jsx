import axios from 'axios';
import React, {useState} from 'react'
import Swal from "sweetalert2";
import {useNavigate} from "react-router-dom";

function SignupPage() {

    const [memberId, setMemberId] = useState('')
    const [memberName, setMemberName] = useState('')
    const [password, setPassword] = useState('')
    const [passwordCheck, setPasswordCheck] = useState('')
    const [phoneNumber, setPhoneNumber] = useState('')
    const [memberIdCheck, setMemberIdCheck] = useState(false)
    const navigate = useNavigate()


    function memberIdHandle(e) {
        setMemberId(e.target.value)
    }

    function memberNameHandle(e) {
        setMemberName(e.target.value)
    }

    function passwordHandle(e) {
        setPassword(e.target.value)
    }

    function passwordCheckHandle(e) {
        setPasswordCheck(e.target.value)
    }

    function phoneNumberHandle(e) {
        setPhoneNumber(e.target.value)
    }


    function duplicateBtnClick() {
        console.log(memberId)
        if (memberId === '') {
            Swal.fire({
                title: '실패',
                html: '아이디를 입력해주세요.',
                icon: 'warning',
                confirmButtonText: '확인'
            })
            return false;
        }

        if (memberId.length < 8 || memberId.length > 20) {
            Swal.fire({
                title: '실패',
                html: "아이디는 8자에서 20자 사이어야 합니다.",
                icon: 'warning',
                confirmButtonText: '확인'
            });
            return false;
        }

        const regex = /^[a-z0-9]*$/;
        if (!regex.test(memberId)) {
            Swal.fire({
                title: '실패',
                html: "아아디는 영문 소문자로만 또는 영문 소문자와 숫자의 조합으로 이루어져야 합니다.",
                icon: 'warning',
                confirmButtonText: '확인'
            });
            return false;
        }


        axios.get('http://localhost:8080/api/v1/duplicate/'+memberId)
            .then(

                Swal.fire({
                title: '성공',
                html: "사용가능한 아이디 입니다!",
                icon: 'warning',
                confirmButtonText: '확인'
            }).then((result) => {
                    setMemberIdCheck(true);
                    // if(result.isConfiremd)
                }))


            .catch(error => {
                const data = error.response.data
                if (data.getCode === 'INTERNAL_SERVER_ERROR') {
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
                return false;
            })
    }

    function submitBtnClick() {
        alert("기능개발중입니다!")
    }


    return (
        <div className="signup-page">
            <div className="signup-wrapper">
                <h2>회원가입을 위해<br/>정보를 입력해주세요.</h2><br/><br/>
                <span className="ft-lightgrey">* 아이디</span><br/><br/>
                <div className="input-id-wrapper">
                    <input className="input-id" onChange={(e) => memberIdHandle(e)}/>
                    <button className="duplicate-button" onClick={() => duplicateBtnClick()}>아이디 중복 확인</button>
                </div>

                <span className="ft-lightgrey">*이름</span><br/><br/>
                <input className="input" onChange={(e) => memberNameHandle(e)}/>

                <span className="ft-lightgrey">* 비밀번호</span><br/><br/>
                <input className="input" type="password" onChange={(e) => passwordHandle(e)}/>

                <span className="ft-lightgrey">* 비밀번호 확인</span><br/><br/>
                <input className="input" type="password" onChange={(e) => passwordCheckHandle(e)}/>

                <span className="ft-lightgrey">* 전화번호</span><br/><br/>
                <input className="input" onChange={(e) => phoneNumberHandle(e)}/>
                <div style={{textAlign: 'left'}}>
                    <input type="radio" className="radio" name="gender"/><span className="ft-bold">남성</span>
                    <input type="radio" className="radio" name="gender"/><span className="ft-bold">여성</span>
                </div>
                <br/><br/>
                <div style={{textAlign: 'left'}}>
                    <input type="checkbox" className="agree"/><span
                    className="ft-bold">이용약관 개인정보 수집 및 정보이용에 동의합니다.</span><br/><br/><br/>
                </div>
                <button className="submit-button" onClick={() => submitBtnClick()}><span className="ft-sm ft-bold">가입하기</span></button>
            </div>
        </div>
    )
}

export default SignupPage;
