import { useNavigate } from 'react-router-dom';
import '../index.css'
function TestPage() {
  const navigate = useNavigate();

  // Navigation handlers
  function handleLogin() {
    navigate('/Login'); 
  };

  function handleSignUp() {
    navigate('/SignUp'); 
  };

  function handleForgotPassword() {
    navigate('/ForgotPasswordPage');
  };

  function handleHomepage() {
    navigate('/Homepage'); 
  };

  function handleNavigateScreen() {
    navigate('/NavigateScreen'); 
  };

  function handleSettingsPage() {
    navigate('/SettingsPage');
  };

  return (
    <div>
      <h1>This is the Test Page.</h1>
      <div style={{ display: 'flex', justifyContent: 'center' }}>
      <button onClick={handleLogin}>Test Login Page</button>
      <button onClick={handleSignUp}>Test Signup Page</button>
      <button onClick={handleHomepage}>Test Homepage</button>
      <button onClick={handleForgotPassword}>Test Forgot Password Page</button>
      <button onClick={handleNavigateScreen}>Test Navigate Page</button>
      <button onClick={handleSettingsPage}>Test Settings Page</button>
      </div>
    </div>
  );
}

export default TestPage;

export function useTestNavigation() {
  const navigate = useNavigate();
  return {
    handleTestPage: () => navigate('/')
  };
}