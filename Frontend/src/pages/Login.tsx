import { useState } from 'react';
import {useNavigate} from 'react-router-dom';
import './Login.css'; 

function Login () {
  
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  function handleLogin() {
    //Redirects to the Homepage if login successful
    navigate('/Homepage');
  };

  function handleSignUp()  {
    //Redirecting to the sign up page.
    navigate('/Sign Up');
  };

  function handleForgotPassword() {
    //Redirects to forgot password page
    navigate('/ForgotPasswordPage');
  };

  return (
      <div className="login-box">
        <div className="form-container">
          <div className="logo">
            <img src="/demoLogo.png" alt="Logo" /> 
          </div>
          <input
            type="text"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <button onClick={handleLogin}>Log In</button>
          <div className="additional-options">
            <button onClick={handleSignUp}>Sign Up</button>
            <button onClick={handleForgotPassword}>Forgot Password?</button>
          </div>
        </div>
      </div>
  );
};

export default Login;