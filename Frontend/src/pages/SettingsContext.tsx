// src/context/SettingsContext.tsx
import React, { createContext, useContext, useState, ReactNode } from 'react';

interface Settings {
  theme: 'light' | 'dark';
  notifications: boolean;
}

interface SettingsContextType {
  settings: Settings;
  updateSettings: (newSettings: Partial<Settings>) => void;
}

const SettingsContext = createContext<SettingsContextType | undefined>(undefined);

export function SettingsProvider({ children }: { children: ReactNode }) {
  const [settings, setSettings] = useState<Settings>({
    theme: (localStorage.getItem('appSettings') ? JSON.parse(localStorage.getItem('appSettings')!).theme : 'light') as 'light' | 'dark',
    notifications: (localStorage.getItem('appSettings') ? JSON.parse(localStorage.getItem('appSettings')!).notifications : true) as boolean,
  });

  const updateSettings = (newSettings: Partial<Settings>) => {
    setSettings((prev) => ({
      ...prev,
      ...newSettings,
    }));
    localStorage.setItem('appSettings', JSON.stringify({ ...settings, ...newSettings }));
  };

  return (
    <SettingsContext.Provider value={{ settings, updateSettings }}>
      {children}
    </SettingsContext.Provider>
  );
}

export const useSettings = () => {
  const context = useContext(SettingsContext);
  if (context === undefined) {
    throw new Error('useSettings must be used within a SettingsProvider');
  }
  return context;
};