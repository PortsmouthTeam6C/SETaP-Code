import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './SignUp.css';

function SignUpPage() {
  const navigate = useNavigate();
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [isChecked, setIsChecked] = useState(false);

  function handleSignUp() {
    if (password !== confirmPassword) {
      alert("Passwords do not match!");
      return;
    }
    console.log("User signed up:", { firstName, lastName, username });
    navigate('/');
  }

  return (
    <div className="SignUp-box">
     <div className="form-container">
      <img src="/demoLogo.png" alt="Logo" className = "project-Logo"/> 
        <div className="Title">
        <h1>Sign Up</h1>
        </div>
        <h3>First Name</h3>
        <input
          type="text"
          placeholder="First Name"
          value={firstName}
          onChange={(e) => setFirstName(e.target.value)}
        />
        <h3>Last Name</h3>
        <input
          type="text"
          placeholder="Last Name"
          value={lastName}
          onChange={(e) => setLastName(e.target.value)}
        />
        <h3>Email</h3>
        <input
          type="text"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
        <h3>Password</h3>
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <h3>Confirm Password</h3>
        <input
          type="password"
          placeholder="Confirm Password"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
        />
        <div className="checkbox-container">
          <input
            type="checkbox"
            id="terms"
            checked={isChecked}
            onChange={(e) => setIsChecked(e.target.checked)}
          />
          <label htmlFor="terms">I accept the Terms and Conditions</label>
        </div>
        <button onClick={handleSignUp}>Sign Up</button>
      </div>
    </div>
  );
}

export default SignUpPage;
