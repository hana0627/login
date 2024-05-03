import './App.css';
import {Route, Routes} from 'react-router-dom'
import LoginPage from "./pages/LoginPage";
import SignupPage from "./pages/SignupPage";
import MyPage from "./pages/MyPage"
import styleCss from "./style.css"
import './style.css';
import TokenRenew from "./component/TokenRenew";

function App() {
    return (
        <div className="styleCss">
            <Routes>
                <TokenRenew/>
                <Route path='/login' element={<LoginPage/>}/>
                <Route path='/SignupPage' element={<SignupPage/>}/>
                <Route path='/MyPage' element={<MyPage/>}/>
                <Route path='/*' element={<div>없는페이지에옹</div>}/>
            </Routes>
        </div>
    );
}

export default App;
