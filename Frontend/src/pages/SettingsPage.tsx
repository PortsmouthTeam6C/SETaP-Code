import { useTheme } from "../context/themeContext";
import { useNavigate } from "react-router-dom";


export default function SettingsPage() {
  const navigate = useNavigate();
  const { theme, setTheme } = useTheme();

  const toggleTheme = () => {
    console.log("Before toggle - Current theme:", theme); /* Debug */
    const newTheme = theme === "light" ? "dark" : "light";
    setTheme(newTheme);
    console.log("After toggle - New theme:", newTheme); /* Debug*/
  };

  function handleHomepage() {
    navigate('/Homepage'); 
  };

  return (
    <div style={{ padding: "20px", maxWidth: "400px", margin: "0 auto" }}>
      <h2>Settings</h2>
      <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', justifyContent: 'center' }}>
        <button onClick={toggleTheme}>Toggle Theme (Current: {theme})</button>
        <button onClick={handleHomepage}>Back to Homepage</button>
      </div>
    </div>
  );
}