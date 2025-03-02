import React from 'react';
import './SettingsPage.css';
import { Link } from 'react-router-dom';
import { useSettings } from './SettingsContext'; 

export default function SettingsPage(): JSX.Element {
  const { settings, updateSettings } = useSettings(); // Use context

  const handleThemeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const newTheme = e.target.value as 'light' | 'dark';
    updateSettings({ theme: newTheme }); // Update context, which handles localStorage
  };

  const handleNotificationsChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    updateSettings({ notifications: e.target.checked }); // Update context
  };

  return (
    <div className={`container ${settings.theme === 'dark' ? 'dark-mode' : ''}`}>
      <div className="content-panel">
        <h1 className="settings-title">Settings</h1>
        <div className="settings-options">
          {/* Theme Toggle */}
          <div className="setting-item">
            <label className="text-gray-700">Theme:</label>
            <select
              value={settings.theme} // Use settings.theme from context
              onChange={handleThemeChange} // Update via context
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
              checked={settings.notifications} // Use settings.notifications from context
              onChange={handleNotificationsChange} // Update via context
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