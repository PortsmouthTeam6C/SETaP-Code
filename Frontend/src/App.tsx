import Login from './pages/Login'
import TestPage from './pages/TestPage'
import Homepage from './pages/Homepage'
import SignupPage from './pages/SignupPage'
import ForgotPasswordPage from './pages/ForgotPasswordPage'
import './App.css'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<TestPage />} />
        <Route path="/Login" element = {<Login />} />
        <Route path = "/Sign Up" element = {<SignupPage />} />
        <Route path = "/Homepage" element = {<Homepage />} />
        <Route path = "/ForgotPasswordPage" element = {<ForgotPasswordPage />} />
      </Routes>
    </Router>
  );
}
export default App
