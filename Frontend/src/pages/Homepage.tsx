import { useState } from "react";
import { useNavigate } from 'react-router-dom';
import './HomePage.css';

const societies = [
  { id: 1, name: "Photography Club" },
  { id: 2, name: "Coding Society" },
  { id: 3, name: "Music Club" },
];

const events = [
  { id: 1, picture: "📷", description: "Photography Workshop", date: "March 10", time: "2 PM", place: "Room 101", price: "$5" },
  { id: 2, picture: "💻", description: "Hackathon", date: "March 15", time: "10 AM", place: "Main Hall", price: "Free" },
  { id: 3, picture: "🎵", description: "Live Music Night", date: "March 20", time: "7 PM", place: "Cafeteria", price: "$10" },
];

const initialMessages = [
  { id: 1, user: "Alice", text: "Hey everyone! Looking forward to the event." },
  { id: 2, user: "Bob", text: "Can't wait! It's going to be amazing." },
  { id: 3, user: "Charlie", text: "Does anyone know the entry requirements?" },
];

function Homepage() {
  const navigate = useNavigate();
  const [messages, setMessages] = useState(initialMessages);
  const [newMessage, setNewMessage] = useState("");

  function handleJoinSociety() {
    // Redirects to navigate page
    navigate('/NavigateScreen');
  }

  function handleSendMessage() {
    if (newMessage.trim() !== "") {
      const newMessageObj = {
        id: messages.length + 1,  // Simple way to create a unique id
        user: "You",  // Assuming the user sending the message is always "You" for simplicity
        text: newMessage
      };

      // Update messages state
      setMessages((prevMessages) => [...prevMessages, newMessageObj]);

      // Clear input field after sending the message
      setNewMessage("");
    }
  }

  function handleKeyDown(event: { key: string; }) {
    if (event.key === 'Enter') {
      handleSendMessage();
    }
  }

  return (
    <div className="container">
      {/* Society Section */}
      <div className="society">
        <h2 className="title">Societies</h2>
        <div className="society-list">
          {societies.map((society) => (
            <div key={society.id} className="society-item">
              <p>{society.name}</p>
            </div>
          ))}
        </div>
        {/* Join Society Button */}
        <div className="add-society">
          <button onClick={handleJoinSociety}>+ Join Society</button>
        </div>
      </div>

      {/* Messages Section */}
      <div className="messages">
        <h2 className="title">Messages</h2>
        <div className="message-box">
          {messages.map((msg) => (
            <p key={msg.id}><strong>{msg.user}:</strong> {msg.text}</p>
          ))}
        </div>

        {/* Message Input and Send Button */}
        <div className="message-input">
          <input className="enter-message"
            type="text"
            value={newMessage}
            onChange={(e) => setNewMessage(e.target.value)}
            onKeyDown={handleKeyDown}
            placeholder="Type your message..."
          />
          <button onClick={handleSendMessage}>Send</button>
        </div>
      </div>

      {/* Event Board Section */}
      <div className="event">
        <h2 className="title">Event Board</h2>
        {events.map((event) => (
          <div key={event.id} className="event-item">
            <p>{event.picture} <span>{event.description}</span></p>
            <p className="event-details">{event.date}, {event.time} - {event.place}</p>
            <p className="event-price">Price: {event.price}</p>
          </div>
        ))}
      </div>
    </div>
  );
}

export default Homepage;
