import { createContext, useContext, useState, ReactNode } from "react";

type Theme = "light" | "dark";

// Define context type
interface ThemeContextType {
  theme: Theme;
  setTheme: (theme: Theme) => void;
}

// Create context with a default value
const ThemeContext = createContext<ThemeContextType>({
    theme: "light", // Default theme
    setTheme: () => {}, // Waiting for value to be set by user
  });

// Theme provider component
export const ThemeProvider = ({ children }: { children: ReactNode }) => {
  const [theme, setTheme] = useState<Theme>("light"); // Default to light theme
  console.log("ThemeProvider - Current theme:", theme); // Debug

  return (
    <ThemeContext.Provider value={{ theme, setTheme }}>
      {children}
    </ThemeContext.Provider>
  );
};

// Custom hook to use theme context
export const useTheme = () => {
  const context = useContext(ThemeContext);
  if (!context) {
    throw new Error("useTheme must be used within a ThemeProvider");
  }
  return context;
};