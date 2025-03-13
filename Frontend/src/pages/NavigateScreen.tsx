import { useState } from 'react';
import './NavigateScreen.css';  
import { Link } from "react-router-dom";

{/* Dropdown menu */}
import { FaChevronDown, FaChevronUp, FaSignOutAlt, FaUserEdit } from "react-icons/fa";
interface SingleLevelDropdownMenuProps {
  buttonLabel: string;
  items: {
    title: string;
    url?: string;
    icon?: JSX.Element;
    action?: () => void;
  }[];
}
export const SingleLevelDropdownMenu = ({
  buttonLabel,
  items,
}: SingleLevelDropdownMenuProps) => {
  const [open, setOpen] = useState(false);
  const handleToggle = () => {
    setOpen((prev) => !prev);
  };
  return (
    <div className="relative">
      <button
  type="button"
  className="inline-flex items-center justify-center rounded-md text-sm border border-gray-300 h-10 px-4 py-2 bg-gray-100 text-gray-800 hover:bg-gray-300 hover:text-gray-900 transition"
  onClick={handleToggle}
>
  {buttonLabel}
  <span className="ml-2">
    {open ? <FaChevronUp /> : <FaChevronDown />}
  </span>
</button>
      {open && (
        <div className="absolute left-1/2 -translate-x-1/2 top-12 w-56 bg-white border border-gray-200 shadow-lg rounded-md p-1">
        <ul className="w-full h-auto">
          {items.map((item, index) => (
            <li
              key={index}
              className="flex items-center gap-2 px-4 py-2 text-sm hover:bg-gray-100 rounded-md cursor-pointer transition"
            >
              {item.icon && <span className="text-gray-600">{item.icon}</span>}
              {item.url ? (
                <Link
                  to={item.url}
                  className="w-full text-left text-gray-800 hover:text-gray-900"
                  onClick={() => setOpen(false)}
                >
                  {item.title}
                </Link>
              ) : (
                <button
                  className="w-full text-left text-gray-800 hover:text-gray-900"
                  onClick={() => {
                    item.action?.();
                    setOpen(false);
                  }}
                  type="button"
                >
                  {item.title}
                </button>
              )}
            </li>
          ))}
        </ul>
      </div>      
      )}
    </div>
  );
};

const societies = [
  { id: 1, name: "Photography Club", price: "$10", description: "A club for photography lovers." },
  { id: 2, name: "Coding Society", price: "Free", description: "Join us to code cool projects." },
  { id: 3, name: "Music Club", price: "$15", description: "For musicians and music lovers." },
  { id: 4, name: "Photography Club", price: "$10", description: "A club for photography lovers." },
  { id: 5, name: "Coding Society", price: "Free", description: "Join us to code cool projects." },
  { id: 6, name: "Music Club", price: "$15", description: "For musicians and music lovers." },
];

export default function NavigateScreen() {
  const [selectedSociety, setSelectedSociety] = useState(null);
  const [searchQuery, setSearchQuery] = useState("");

  // Filter societies based on search query
  const filteredSocieties = societies.filter(society =>
    society.name.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="container">
      {/* Left Section: Search & Society List */}
      <div className="left-section">
        {/* Search Bar */}
        <input
          type="text"
          placeholder="Search..."
          className="search-bar"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
        />
        
        {/* Scrollable Society List */}
        <div className="society-list">
          {filteredSocieties.length > 0 ? (
            filteredSocieties.map((society) => (
              <div
                key={society.id}
                className={`society-item ${selectedSociety?.id === society.id ? "selected" : ""}`}
                onClick={() => setSelectedSociety(society)}
              >
                <h3>{society.name}</h3>
                <p>{society.price}</p>
              </div>
            ))
          ) : (
            <p className="text-gray-500 text-center">No societies found</p>
          )}
        </div>
      </div>

      {/* Right Section: Society Details */}
      <div className="right-section">
        {selectedSociety ? (
          <>
            <h2>{selectedSociety.name}</h2>
            <p>{selectedSociety.description}</p>
            <p className="price">Price: {selectedSociety.price}</p>
          </>
        ) : (
          <p className="text-gray-500">Select a society to see details</p>
        )}
      </div>
      <div className='profile-button'>
            {/* Profile Dropdown */}
            <SingleLevelDropdownMenu
        buttonLabel="Profile"
        items={[
          { title: "   Settings", url: "/SettingsPage", icon: <FaUserEdit /> },
          { title: "   Logout", url: "/Login", icon: <FaSignOutAlt /> },
        ]}
      />
      </div>
    </div>
    
  );
}
