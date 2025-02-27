import { useState } from "react";
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

const messages = [
  { id: 1, user: "Alice", text: "Hey everyone! Looking forward to the event." },
  { id: 2, user: "Bob", text: "Can't wait! It's going to be amazing." },
  { id: 3, user: "Charlie", text: "Does anyone know the entry requirements?" },
];

export default function Homepage() {
  return (
    <div className="container">
      {/* Society List */}
      <div className="card">
        <h2 className="title">Societies</h2>
        {societies.map((society) => (
          <div key={society.id} className="society-item">
            <p>{society.name}</p>
          </div>
        ))}
      </div>

      {/* Messages */}
      <div className="card">
        <h2 className="title">Messages</h2>
        <div className="message-box">
          {messages.map((msg) => (
            <p key={msg.id}><strong>{msg.user}:</strong> {msg.text}</p>
          ))}
        </div>
      </div>

      {/* Event Board */}
      <div className="card">
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
