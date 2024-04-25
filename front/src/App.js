import './App.css';
import {Route, Routes} from 'react-router-dom'
import LoginPage from "./pages/LoginPage";
function App() {
  return (
      <div className="App">
          <Routes>
              <Route path='/login' element={<LoginPage/>} />
              <Route path='/*' element={<div>없는페이지에옹</div>} />
          </Routes>
      </div>
  );
}

export default App;
