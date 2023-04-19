import { useContext } from 'react';
import './App.scss';
import Home from './components/Home';
import Login from './components/Login';
import Register from './components/Register';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthContext } from './context/AuthContext';

function App() {
  const {currentUser} = useContext(AuthContext)
  const ProtectedRoute = ({children}) => {
    if(!currentUser) {
      return <Navigate to="/ChatApplication/login" />
    }
    return children
  }

  return (
    <BrowserRouter>
      <Routes>
        <Route path='/ChatApplication/'>
          <Route index element={<ProtectedRoute> <Home /> </ProtectedRoute>} />
          <Route path="/ChatApplication/login" element={<Login />} />
          <Route path="/ChatApplication/register" element={<Register />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
