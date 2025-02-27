import { useState } from 'react';
import './NavigateScreen.css';  // Import the CSS file

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

      {/* Profile Button */}
      <div className="profile-button">
        Profile
      </div>
    </div>
  );
}
