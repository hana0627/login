import React, {useEffect, useState} from 'react'
import axios from "axios";
import Swal from "sweetalert2";
import {useNavigate} from "react-router-dom";

function MyPage() {

    const base_url = process.env.REACT_APP_API_URL

    const [isRender, setIsRender] = useState(false)
    const [userId, setUserId] = useState(false)
    const [userName, setUserName] = useState(false)
    const [phoneNumber, setPhoneNumber] = useState(false)
    const navigate = useNavigate()

    const accessToken = localStorage.getItem('accessToken')
    function getData() {
        // axios.get('http://localhost:8080/api/v2/auth',{headers: {Authorization: accessToken}})
        axios.get(base_url+'/api/v2/auth',{headers: {Authorization: accessToken}})
            .then(response => {
                console.log("성공!")
                console.log(response)
                setIsRender(true)
                const data = response.data
                setUserId(data.result.userId)
                setUserName(data.result.userName)
                setPhoneNumber(data.result.phoneNumber)
                // TODO redux 혹은 recoil 과같은 전역변수로 저장할수도 있음
                // 해볼까...? 어차피 리액트 여기까지 다룬거...

            })
            .catch(error => {
                console.log("에러!")
                console.log(error)
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

    function logOutBtnClick() {
        axios.get(base_url+'/api/v2/logout',{headers: {Authorization: accessToken}})
            .then(response => {
                localStorage.removeItem("accessToken")
                Swal.fire({
                    title: '안내',
                    html: '로그아웃 되었습니다',
                    icon: 'warning',
                    confirmButtonText: '확인'
                }).then(response => {
                    if (response.isConfirmed) {
                        navigate('/login');
                    }
                });
            })
            .catch(error => {
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
            })
    }


    useEffect(() => {
        getData()
    },[])



    return (
        <div>
            {isRender?
                <div>
                    <div className="signup-page">
                        <div className="signup-wrapper">
                            {/*<h2>${name} 님 <br/>반갑습니다.</h2><br/><br/>*/}
                            <h2>{userName} 님 <br/>반갑습니다.</h2><br/><br/>

                            <span className="ft-sm ft-bold">선생님의 핸드폰 번호는</span>
                            <span className="ft-sm ft-bold">{phoneNumber} 에요</span><br/><br/>
                            <span className="ft-sm ft-bold">마음의 눈으로 보았을때는 예쁜 화면이랍니다</span>
                            <br/><br/>
                            <hr/>
                            <br/><br/>
                            <button className="submit-button" onClick={() =>logOutBtnClick()}><span
                                className="ft-sm ft-bold">로그아웃</span></button>
                </div>
                    </div>

                </div>:
                <div>로딩중입니다</div>
            }
        </div>
    )
}

export default MyPage;
