import Login from './pages/Login';
import TestPage from './pages/TestPage';
import Homepage from './pages/Homepage';
import SignupPage from './pages/SignupPage';
import ForgotPasswordPage from './pages/ForgotPasswordPage';
import NavigateScreen from './pages/NavigateScreen';
import SettingsPage from './pages/SettingsPage';

import './App.css';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { useTheme } from './context/themeContext'; // Import useTheme
import { useEffect } from 'react'; // Import useEffect

function App() {
  const { theme } = useTheme(); // Get the current theme

  useEffect(() => {
    console.log("Setting data-theme:", theme); // Debug
    document.documentElement.setAttribute('data-theme', theme);
  }, [theme]); // Re-run when theme changes

  return (
    <Router>
      <Routes>
        <Route path="/" element={<TestPage />} />
        <Route path="/Login" element={<Login />} />
        <Route path="/SignUp" element={<SignupPage />} />
        <Route path="/Homepage" element={<Homepage />} />
        <Route path="/ForgotPasswordPage" element={<ForgotPasswordPage />} />
        <Route path="/NavigateScreen" element={<NavigateScreen />} />
        <Route path="/SettingsPage" element={<SettingsPage />} />
      </Routes>
    </Router>
  );
}

export default App;