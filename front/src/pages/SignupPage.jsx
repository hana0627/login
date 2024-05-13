import axios from 'axios';
import React, {useState} from 'react'
import Swal from "sweetalert2";
import {useNavigate} from "react-router-dom";

function SignupPage() {

    const base_url = process.env.REACT_APP_API_URL

    const [userId, setUserId] = useState('')
    const [userName, setUserName] = useState('')
    const [password, setPassword] = useState('')
    const [passwordCheck, setPasswordCheck] = useState('')
    const [phoneNumber, setPhoneNumber] = useState('')
    const [userIdCheck, setUserIdCheck] = useState(false)
    const [gender, setGender] = useState('')
    const [agreed, setAgreed] = useState(false);

    const navigate = useNavigate()


    function userIdHandle(e) {
        setUserId(e.target.value)
        setUserIdCheck(false); // id 입력값 변경시 중복확인인증 해제
    }

    function userNameHandle(e) {
        setUserName(e.target.value)
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

    function handleGenderChange(e) {
        setGender(e);
    }

    function handleAgreeChange(e) {
        setAgreed(e.target.checked);
    }

    function duplicateBtnClick() {
        if (userId === '') {
            Swal.fire({
                title: '실패',
                html: '아이디를 입력해주세요.',
                icon: 'warning',
                confirmButtonText: '확인'
            })
            return false;
        }

        if (userId.length < 8 || userId.length > 20) {
            Swal.fire({
                title: '실패',
                html: "아이디는 8자에서 20자 사이어야 합니다.",
                icon: 'warning',
                confirmButtonText: '확인'
            });
            return false;
        }

        const regex = /^[a-z0-9]*$/;
        if (!regex.test(userId)) {
            Swal.fire({
                title: '실패',
                html: "아아디는 영문 소문자로만 또는 영문 소문자와 숫자의 조합으로 이루어져야 합니다.",
                icon: 'warning',
                confirmButtonText: '확인'
            });
            return false;
        }


        axios.get(base_url+'/api/v1/duplicate/' + userId)
            .then(
                Swal.fire({
                    title: '성공',
                    html: "사용가능한 아이디 입니다!",
                    icon: 'warning',
                    confirmButtonText: '확인'
                })
                    .then(result => {
                        if (result.isConfirmed) {
                            setUserIdCheck(true);
                        }
                    })
            ).catch(error => {
            const data = error.response.data
            if (data.getCode === 'INTERNAL_SERVER_ERROR') {
                Swal.fire({
                    title: '실패',
                    html: '예상하지 못한 에러가 발생했습니다.<br>다시 시도해주세요',
                    icon: 'warning',
                    confirmButtonText: '확인'
                });
            } else {
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
        if (!userIdCheck) {
            Swal.fire({
                title: '실패',
                html: '아이디 중복확인을 진행해주세요.',
                icon: 'warning',
                confirmButtonText: '확인'
            });
            return false;
        }
        if (userName === '') {
            Swal.fire({
                title: '실패',
                html: '이름을 입력해주세요.',
                icon: 'warning',
                confirmButtonText: '확인'
            })
            return false;
        }

        if (password === '') {
            Swal.fire({
                title: '실패',
                html: '비밀번호를 입력해주세요.',
                icon: 'warning',
                confirmButtonText: '확인'
            })
            return false;
        }

        if (passwordCheck === '') {
            Swal.fire({
                title: '실패',
                html: '비밀번호 확인을 입력해주세요.',
                icon: 'warning',
                confirmButtonText: '확인'
            })
            return false;
        }

        if (password !== passwordCheck) {
            Swal.fire({
                title: '실패',
                html: '비밀번호가 일치하지 않습니다.',
                icon: 'warning',
                confirmButtonText: '확인'
            })
            return false;
        }
        if (phoneNumber === '') {
            Swal.fire({
                title: '실패',
                html: '전화번호를 입력해주세요',
                icon: 'warning',
                confirmButtonText: '확인'
            })
            return false;
        }

        if (gender === '') {
            Swal.fire({
                title: '실패',
                html: '성별을 선택해주세요',
                icon: 'warning',
                confirmButtonText: '확인'
            })
            return false;
        }

        if (agreed !== true) {
            Swal.fire({
                title: '실패',
                html: '약관에 동의해주세요',
                icon: 'warning',
                confirmButtonText: '확인'
            })
            return false;
        }
        axios.post(base_url+'/api/v1/join',
            {
                'userId': userId,
                'userName': userName,
                'password': password,
                'phoneNumber': phoneNumber,
                'gender': gender
            }).then(() => {
            Swal.fire({
                title: '성공',
                html: '회원가입이 완료되었습니다.',
                icon: 'warning',
                confirmButtonText: '확인'
            }).then(result => {
                if (result.isConfirmed) {
                    navigate('/login');
                }
            });
        })
            .catch((error) => {
                const data = error.response.data
                if (data.getCode === 'INTERNAL_SERVER_ERROR') {
                    Swal.fire({
                        title: '실패',
                        html: '예상하지 못한 에러가 발생했습니다.<br>다시 시도해주세요',
                        icon: 'warning',
                        confirmButtonText: '확인'
                    });
                } else {
                    Swal.fire({
                        title: '실패',
                        html: data.getMessage,
                        icon: 'warning',
                        confirmButtonText: '확인'
                    });
                }
            })

    }


    return (
        <div className="signup-page">
            <div className="signup-wrapper">
                <h2>회원가입을 위해<br/>정보를 입력해주세요.</h2><br/><br/>
                <span className="ft-lightgrey">* 아이디</span><br/><br/>
                <div className="input-id-wrapper">
                    <input className="input-id" onChange={(e) => userIdHandle(e)}/>
                    <button className="duplicate-button" onClick={() => duplicateBtnClick()}>아이디 중복 확인</button>
                </div>

                <span className="ft-lightgrey">*이름</span><br/><br/>
                <input className="input" onChange={(e) => userNameHandle(e)}/>

                <span className="ft-lightgrey">* 비밀번호</span><br/><br/>
                <input className="input" type="password" onChange={(e) => passwordHandle(e)}/>

                <span className="ft-lightgrey">* 비밀번호 확인</span><br/><br/>
                <input className="input" type="password" onChange={(e) => passwordCheckHandle(e)}/>

                <span className="ft-lightgrey">* 전화번호</span><br/><br/>
                <input className="input" onChange={(e) => phoneNumberHandle(e)}/>
                <div style={{textAlign: 'left'}}>

                    <input type="radio" value="M" checked={gender === 'M'} onChange={() => handleGenderChange('M')}/>
                    <span onChange={() => handleGenderChange('M')}>남성</span>
                    <input type="radio" value="F" checked={gender === 'F'} onChange={() => handleGenderChange('F')}/>
                    <span onChange={() => handleGenderChange('F')}>여성</span>
                </div>
                <br/><br/>
                <div style={{textAlign: 'left'}}>
                    <input type="checkbox" checked={agreed} onChange={(e) => handleAgreeChange(e)}/><span
                    className="ft-bold">이용약관 개인정보 수집 및 정보이용에 동의합니다.</span><br/><br/><br/>
                </div>
                <button className="submit-button" onClick={() => submitBtnClick()}><span
                    className="ft-sm ft-bold">가입하기</span></button>
            </div>
        </div>
    )
}

export default SignupPage;
