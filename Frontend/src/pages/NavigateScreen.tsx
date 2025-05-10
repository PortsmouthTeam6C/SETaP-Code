import { useState } from 'react';
import './NavigateScreen.css';  
import { Link } from "react-router-dom";
import { useNavigate } from 'react-router-dom';
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
  { id: 1, name: "Calistenics Society", description: `Would you like to defy gravity?

Calisthenics is all about building strength and skill through the use of bodyweight exercises. We aim to promote the physical and mental well-being of all our members.

At University of Portsmouth Calisthenics everyone is welcome to train with us regardless of their experience or skill level. All our sessions are run by our members who have knowledge and experience in a number of disciplines including gymnastics, martial arts, athletics, yoga, bodybuilding and personal training which makes training informative and fun. We are an easy-going friendly bunch of students who thrive on supporting each other to reach the various goals we set ourselves throughout the year.` },
  { id: 2, name: "Book Club Society", description: `We're a fun, laid-back group for students who love books and good vibes! 📚✨ We meet up to chat about everything—from bestsellers to hidden gems—and host chill events like book swaps, themed discussions, and coffee hangouts. ☕️ Whether you're a casual reader or totally obsessed, it's the perfect place to make new friends, share recs, and dive into amazing stories together.` },
  { id: 3, name: "Board Games Society", description: `Hello and welcome! You've found your way to the page for all things boardy and gamey at Portsmouth. We're so glad you've made it.

We are a non-paid, no-stress society devoted to the playing and enjoyment of board games. Regardless of how committed to the board games grind you are, the course you're on, or how many deadlines you have due next week, we always meet on a Sunday morning (11am start) in the Union building.

To keep up to date please join our Discord server for all the most up-to-date news!` },
  { id: 4, name: "Basic Self Defense Society", description: `Welcome to a new year at the Basic Self Defence Society!

At the Basic Self Defence Society, we offer a wide range of martial arts and self defence systems including: aikido, boxing, Brazilian jiu-jitsu, eskrima, judo, and krav maga. Our free membership includes access to weekly sessions and workshops.

The techniques you acquire in these sessions will not only improve your focus but develop your ability to confidently assess situations.` },
  { id: 5, name: "Bodybuilding Society", description: `Welcome to the Official Society for University of Portsmouth Bodybuilding.

We are a welcoming society to anyone who shares and has a passion for Bodybuilding. No matter your shape, size, age, or background, everyone is welcome here within this society!

As a part of this society, we wish and strive to improve your gym knowledge, etiquette, and ability, whilst enjoying being a part of the Bodybuilding family. Through this society, you'll be able to meet like-minded individuals and create lifelong friendships and workout partners.` },
  { id: 6, name: "Music Society", description: `We are the UoP Music Society!

We comprise 4 different ensembles that run Monday-Thursday. Within our rehearsals, we welcome students, staff, and members of the community, and aim to provide a welcoming environment for all.` },
  { id: 7, name: "Photography Society", description: `Welcome to the Photography Society!

Whether you're a seasoned photographer or just starting out, our society is the perfect place to explore your passion for capturing moments. We host regular workshops, photo walks, and exhibitions to help you improve your skills and showcase your work. Join us to meet like-minded individuals and share your love for photography!` },
  { id: 8, name: "Coding Society", description: `Welcome to the Coding Society!

Are you passionate about coding, software development, or just curious about technology? Our society is open to all skill levels, from beginners to advanced programmers. We organize hackathons, coding challenges, and workshops on various programming languages and technologies. Join us to learn, collaborate, and build amazing projects together!` },
];

export default function NavigateScreen() {
  const [selectedSociety, setSelectedSociety] = useState(null);
  const [searchQuery, setSearchQuery] = useState("");
  const navigate = useNavigate();

  // Filter societies based on search query
  const filteredSocieties = societies.filter(society =>
    society.name.toLowerCase().includes(searchQuery.toLowerCase())
  );

  function handleHomepage() {
    navigate('/Homepage');
  };

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
              </div>
            ))
          ) : (
            <p className="text-gray-500 text-center">No societies found</p>
          )}
        </div>
        {/* Back to Homepage Button */}
        <div className="homepage-button">
          <button onClick={handleHomepage}>Back to Homepage</button>
        </div>
      </div>

      {/* Right Section: Society Details */}
      <div className="right-section">
        {selectedSociety ? (
          <>
            <h2>{selectedSociety.name}</h2>
            <div className="society-description">{selectedSociety.description}</div>
          </>
        ) : (
          <p className="text-gray-500">Select a society to see details</p>
        )}
      </div>
    <div className = "profile-section">
      <div className='profile-button'>
            {/* Profile Dropdown */}
            <SingleLevelDropdownMenu
        buttonLabel={<img src = "../../public/ProfilePic.jpg" alt="Profile Pic" className="profile-pic"/>}
        items={[
          { title: "  Settings", url: "/SettingsPage", icon: <FaUserEdit /> },
          { title: "  Logout", url: "/", icon: <FaSignOutAlt /> },
        ]}
      />
      </div>
      </div>
    </div>
    
  );
}
