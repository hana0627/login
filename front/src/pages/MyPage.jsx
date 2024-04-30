import React, {useState} from 'react'
import axios from "axios";
import Swal from "sweetalert2";

function MyPage() {
    
    const [isRender, setIsRender] = useState(false)

    axios.get('http://localhost:8080/api/v1/auth')
        .then(response => {
            setIsRender(true)
        })
        .catch(error => {
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



    return (
        <div>
            {isRender? <div>안녕하세요? 박하나님</div>:
                <div>로딩중입니다</div>
            }
        </div>
    )
}

export default MyPage;
