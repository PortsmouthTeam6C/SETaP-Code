import {useContext, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import './Login.css';
import {login} from "../api/auth/login";
import {UserContext} from "../context/UserContext";

function Login () {
  const context = useContext(UserContext);
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  async function handleLogin() {
    console.log("Requesting login")
    const response = await login(email, password);
    console.log(response);
    if (response !== undefined) {
      console.log("Updating user")
      await context.updateUser(response);
      navigate('/Homepage');
      return;
    }
    alert('Invalid login.')
  }

  // function handleSignUp() {
  //   //Redirecting to the sign up page.
  //   navigate('/SignUp');
  // }
  //
  // function handleForgotPassword() {
  //   //Redirects to forgotten password page
  //   navigate('/ForgotPasswordPage');
  // }

  return <div className={'login-page'}>
      <div className="login-box">
        <div className="form-container">
          <div className="logo">
            <img src="/demoLogo.png" alt="Logo" /> 
          </div>
          <input
            type="text"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <button onClick={handleLogin}>Log In</button>
          {/*<div className="additional-options">*/}
          {/*  <button onClick={handleSignUp}>Sign Up</button>*/}
          {/*  <button onClick={handleForgotPassword}>Forgot Password?</button>*/}
          {/*</div>*/}
        </div>
      </div>
  </div>
}

export default Login;