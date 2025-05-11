import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './ForgotPassword.css';

function ForgotPassword() {
  const navigate = useNavigate();
  const [Email, setEmail] = useState('');

  function handleSignUp() {
    if (Email !== Email) {
      alert("Passwords do not match!");
      return;
    }
    navigate('/');
  }

  return (
    <div className="SignUp-box">
      <div className="form-container">
        <div className="logo">
          <img src="/demoLogo.png" alt="Logo" /> 
        </div>
        
        <div className="Title">
          <h1>Forgot Password</h1>
        </div>
        
        <h3>Enter Email Address</h3>
        <input
          type="text"
          placeholder="Email"
          value={Email}
          onChange={(e) => setEmail(e.target.value)}
        />
        <button onClick={handleSignUp}>Sent</button>
      </div>
    </div>
  );
}

export default ForgotPassword;
