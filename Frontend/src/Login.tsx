import React, { useState } from 'react';
import './Login.css'; 

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const handleLogin = () => {

    console.log('Logging in with:', username, password);
  };

  const handleSignUp = () => {
    console.log('Redirecting to sign-up page');
  };

  const handleForgotPassword = () => {
    console.log('Redirecting to forgot password page');
  };

  return (
    <div className="login-container">
      <div className="login-box">
        <div className="logo">
          <img src="/logo.png" alt="Logo" /> 
        </div>
        <div className="form-container">
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
    </div>
  );
};

export default Login;