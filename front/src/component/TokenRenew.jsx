import {useEffect, useState} from "react";
import {jwtDecode} from "jwt-decode";
import { useLocation } from 'react-router-dom';
import axios from "axios";
import Swal from "sweetalert2";
async function checkToken() {
    try {
        const token = localStorage.getItem('accessToken');
        if (token != null) {
            const decodedToken = jwtDecode(token);

            const currentTime = Math.floor(Date.now() / 1000); // 현재 시간을 초 단위로 변환
            const tokenExpiration = decodedToken.exp; // 토큰 유효기간
            const restTime = tokenExpiration - currentTime;
            const expirationThreshold = 2 * 60;
            // 토큰의 유효시간이 2분 미만이면 토큰갱신 함수 실행
            if (restTime <= expirationThreshold) {
                await renewToken(token);
            }
        }
    } catch (error) {
        console.error('알 수 없는 에러발생')
    }
}

async function renewToken() {
    await axios.get('http://localhost:8080/api/v1/renewToken')
        .then(response => {
            localStorage.setItem('accessToken',response.data);
        }).catch(error => {
            const data = error.response.data
            if(data.getCode === 'INTERNAL_SERVER_ERROR') {
                Swal.fire({
                    title: '실패',
                    html: '예상하지 못한 에러가a 발생했습니다.<br>다시 시도해주세요',
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

function TokenRenew() {
    const location = useLocation();
    const [intervalId, setIntervalId] = useState(null);

    useEffect(() => {
        const interval = setInterval(() => {
            checkToken();
            // 20초마다 실행
        }, 60 * 1000);

        setIntervalId(interval);

        return () => {
            // 컴포넌트 언마운트 시 인터벌 제거
            clearInterval(interval);
        };
    }, []);

    useEffect(() => {
        checkToken();
        // 라우팅 정보 변경 시 실행
    }, [location.pathname]);

    return null;
}
export default TokenRenew;
