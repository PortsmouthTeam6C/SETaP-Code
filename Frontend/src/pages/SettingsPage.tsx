import React, { useState } from 'react';
import './SettingsPage.css';
import { Link } from 'react-router-dom';

export default function SettingsPage(): JSX.Element {
  // State for app-wide settings
  const [theme, setTheme] = useState<'light' | 'dark'>('light'); // TypeScript union type for theme
  const [notifications, setNotifications] = useState<boolean>(true); // Boolean for notifications

  // Function to save settings (e.g., to localStorage)
  const saveSettings = () => {
    localStorage.setItem('appSettings', JSON.stringify({ theme, notifications }));
  };

  // Function to load settings from localStorage (on mount or refresh)
  React.useEffect(() => {
    const savedSettings = localStorage.getItem('appSettings');
    if (savedSettings) {
      const { theme, notifications } = JSON.parse(savedSettings);
      setTheme(theme);
      setNotifications(notifications);
    }
  }, []); // Empty dependency array ensures this runs only once on mount

  return (
    <div className={`container ${theme === 'dark' ? 'dark-mode' : ''}`}>
      <div className="content-panel">
        <h1 className="settings-title">Settings</h1>
        <div className="settings-options">
          {/* Theme Toggle */}
          <div className="setting-item">
            <label className="text-gray-700">Theme:</label>
            <select
              value={theme}
              onChange={(e) => setTheme(e.target.value as 'light' | 'dark')}
              onBlur={saveSettings} // Save when user leaves the input
              className="setting-input"
            >
              <option value="light">Light</option>
              <option value="dark">Dark</option>
            </select>
          </div>

          {/* Notifications Toggle */}
          <div className="setting-item">
            <label className="text-gray-700">Notifications:</label>
            <input
              type="checkbox"
              checked={notifications}
              onChange={(e) => setNotifications(e.target.checked)}
              onBlur={saveSettings} // Save when user leaves the input
              className="setting-checkbox"
            />
          </div>
        </div>
        <Link to="/Homepage" className="custom-link">
          Back to Home
        </Link>
      </div>
    </div>
  );
}