import axios from 'axios';

function LoginPage() {

    function loginBtnClick() {
        axios.get('http://localhost:8080/hello')
            .then((response) => {
                console.log(response.data);
            })
            .catch(() => {
                console.error('에러발생');}
        )
    }

    return (
        <button onClick={()=> loginBtnClick()}>누르고 싶게 생긴 버튼</button>
    )
}


export default LoginPage;
