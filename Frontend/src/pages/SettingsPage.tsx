import { useTheme } from "../context/themeContext.tsx";
import { useTestNavigation } from "./TestPage.tsx";

export default function SettingsPage() {
  const { handleTestPage } = useTestNavigation();
  const { theme, setTheme } = useTheme();

  const toggleTheme = () => {
    console.log("Before toggle - Current theme:", theme);
    const newTheme = theme === "light" ? "dark" : "light";
    setTheme(newTheme);
    console.log("After toggle - New theme:", newTheme);
  };

  return (
    <div style={{ padding: "20px", maxWidth: "400px", margin: "0 auto" }}>
      <h2>Settings</h2>
      <button onClick={toggleTheme}>
        Toggle Theme (Current: {theme})
      </button>
      <button onClick={handleTestPage}>
        Go to Test Page
      </button>
    </div>
  );
}