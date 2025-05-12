import Login from './pages/Login';
import TestPage from './pages/TestPage';
import SignupPage from './pages/SignupPage';
import ForgotPasswordPage from './pages/ForgotPasswordPage';
import NavigateScreen from './pages/NavigateScreen';
import SettingsPage from './pages/SettingsPage';
import OldHomepage from './pages/Homepage';

import './App.css';
import {BrowserRouter as Router, Routes, Route, useLocation, useNavigate} from 'react-router-dom';
import { useTheme } from './context/themeContext';
import {useContext, useEffect} from 'react';
import UserContextProvider from "./context/UserContextProvider";
import {UserContext} from "./context/UserContext";
import Homepage from "./pages/NewHomepage";

function App() {
  const { theme } = useTheme();

  useEffect(() => {
    console.log("Setting data-theme:", theme); // Debug
    document.documentElement.setAttribute('data-theme', theme);
  }, [theme]);

  return (
    <UserContextProvider>
      <Router>
        <Topbar />
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/SignUp" element={<SignupPage />} />
          <Route path="/Homepage" element={<Homepage />} />
          <Route path="/OldHomepage" element={<OldHomepage />} />
          <Route path="/ForgotPasswordPage" element={<ForgotPasswordPage />} />
          <Route path="/NavigateScreen" element={<NavigateScreen />} />
          <Route path="/SettingsPage" element={<SettingsPage />} />
          <Route path="/TestPage" element={<TestPage />} />
        </Routes>
      </Router>
    </UserContextProvider>
  );
}

export function Topbar() {
  const context = useContext(UserContext);
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    console.log(`Context changed. User ${context.username} loaded. ${context.universityLogo}`)
  }, [context]);

  useEffect(() => {
    console.log("Attempt to login with token");
    context.tryLogin().then(success => {
      if (!success) return;

      if (location.pathname === '/')
        navigate('/Homepage');
    });
  }, []);

  return context.username === undefined
    ? <></>
    : <header>
      <img src={context.universityLogo}
           alt={context.universityName}
           className={'university-logo'} />
      <div className={'profile'}>
        <p>{context.username}</p>
        <img src={context.profilePicture}
             alt={"User profile picture"}
             className={'user-profile-picture'} />
      </div>
    </header>
}

export default App;
