import './App.css';
import {Route, Routes} from 'react-router-dom'
import LoginPage from "./pages/LoginPage";
import SignupPage from "./pages/SignupPage";
import styleCss from "./style.css"
import './style.css';
function App() {
  return (
      <div className="styleCss">
          <Routes>
              <Route path='/login' element={<LoginPage/>} />
              <Route path='/SignupPage' element={<SignupPage/>} />
              <Route path='/*' element={<div>없는페이지에옹</div>} />
          </Routes>
      </div>
  );
}

export default App;
