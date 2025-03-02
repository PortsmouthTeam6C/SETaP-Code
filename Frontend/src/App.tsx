import React, { useEffect } from 'react';
import Login from './pages/Login'
import TestPage from './pages/TestPage'
import Homepage from './pages/Homepage'
import SignupPage from './pages/SignupPage'
import ForgotPasswordPage from './pages/ForgotPasswordPage'
import NavigateScreen from './pages/NavigateScreen'
import SettingsPage from './pages/SettingsPage'
import { useSettings } from './pages/SettingsContext'
import './App.css'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'

function App() {
  const { settings } = useSettings();

  useEffect(() => {
    console.log('Current theme:', settings.theme); // Debug
    document.documentElement.setAttribute('data-theme', settings.theme);
  }, [settings.theme]);

  return (
    <Router>
      <Routes>
        <Route path="/" element={<TestPage />} />
        <Route path="/Login" element = {<Login />} />
        <Route path = "/Sign Up" element = {<SignupPage />} />
        <Route path = "/Homepage" element = {<Homepage />} />
        <Route path = "/ForgotPasswordPage" element = {<ForgotPasswordPage />} />
        <Route path = "/NavigateScreen" element = {<NavigateScreen/>} />
        <Route path="/SettingsPage" element={<SettingsPage />} />
      </Routes>
    </Router>
  );
}
export default App;
