import './NewHomepage.css';
import {CiLocationOn, CiShoppingTag} from "react-icons/ci";
import {useColor} from 'color-thief-react'
import {ReactNode, useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";

const societies = [
  { id: 1, name: "Calistenics Society", image: "https://cdn.wildrocket.io/media/05nDddpu1BzUak5pXDfIoeEjQrrM3nj2kDs8V1xt.jpg" },
  { id: 2, name: "Book Club Society", image: "https://cdn.wildrocket.io/media/05nDddpu1BzUak5pXDfIoeEjQrrM3nj2kDs8V1xt.jpg" },
  { id: 3, name: "Board Games Society", image: "https://cdn.wildrocket.io/media/05nDddpu1BzUak5pXDfIoeEjQrrM3nj2kDs8V1xt.jpg" },
  { id: 4, name: "Basic Self Defense Society", image: "https://cdn.wildrocket.io/media/05nDddpu1BzUak5pXDfIoeEjQrrM3nj2kDs8V1xt.jpg" },
  { id: 5, name: "Bodybuilding Society", image: "https://cdn.wildrocket.io/media/05nDddpu1BzUak5pXDfIoeEjQrrM3nj2kDs8V1xt.jpg" },
  { id: 6, name: "Music Society", image: "https://cdn.wildrocket.io/media/05nDddpu1BzUak5pXDfIoeEjQrrM3nj2kDs8V1xt.jpg" },
  { id: 7, name: "Photography Society", image: "https://cdn.wildrocket.io/media/05nDddpu1BzUak5pXDfIoeEjQrrM3nj2kDs8V1xt.jpg" },
  { id: 8, name: "Coding Society", image: "https://cdn.wildrocket.io/media/05nDddpu1BzUak5pXDfIoeEjQrrM3nj2kDs8V1xt.jpg" },
];

const events = [
  {
    title: "Photography Workshop",
    image: "https://i.imgur.com/arBAQXz.jpeg",
    location: "Art Center, Room 101",
    datetime: new Date("2023-11-03T10:00:00"),
    description: "Learn the basics of photography with hands-on practice.",
    price: 5,
  },
  {
    title: "Coding Bootcamp",
    image: "https://images.unsplash.com/photo-1504639725590-34d0984388bd?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    location: "Tech Hub, Main Hall",
    datetime: new Date("2023-11-20T09:00:00"),
    description: "A full-day bootcamp to enhance your coding skills.",
    price: 10,
  },
  {
    title: "Music Jam Session",
    image: "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    location: "Community Hall",
    datetime: new Date("2023-11-18T18:00:00"),
    description: "Join us for an evening of music and collaboration.",
  },
  {
    title: "Board Games Night",
    image: "https://images.unsplash.com/photo-1629760946220-5693ee4c46ac?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    location: "Student Lounge",
    datetime: new Date("2023-11-22T19:00:00"),
    description: "Relax and enjoy a variety of board games with friends.",
    price: 7.50,
  },
  {
    title: "Self Defense Workshop",
    image: "https://images.unsplash.com/photo-1555597673-b21d5c935865?q=80&w=2072&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    location: "Gymnasium",
    datetime: new Date("2023-11-25T14:00:00"),
    description: "Learn essential self-defense techniques in this workshop.",
  },
];

const initialMessages = [
  { id: 1, user: "Alice", text: "Hey everyone! Looking forward to the event. Hey everyone! Looking forward to the event. Hey everyone! Looking forward to the event. Hey everyone! Looking forward to the event. Hey everyone! Looking forward to the event. Hey everyone! Looking forward to the event. Hey everyone! Looking forward to the event." },
  { id: 2, user: "Bob", text: "Can't wait! It's going to be amazing." },
  { id: 3, user: "Charlie", text: "Does anyone know the entry requirements?" },
];

export default function Homepage() {
  return <div className={'page'}>
    <div className={'main-content'}>
      <SocietiesSidebar />
      <ChatPane />
      <EventsSidebar />
    </div>
  </div>
}

function ChatPane() {
  const [messages, setMessages] = useState(initialMessages);
  const [newMessage, setNewMessage] = useState("");

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

  return <main>
    <div className={'messages'}>
      {initialMessages.map((message, key) => <Message user={message.user} key={key}>
        {message.text}
      </Message>)}
    </div>
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
  </main>
}

function Message({ user, children }: { user: string, children: ReactNode }) {
  const picture = "https://api.dicebear.com/9.x/shapes/svg?seed=" + user;
  return <div className={'message'}>
    <img
      src={`${picture}`}
      alt={`${user}'s avatar`}
    />
    <div>
      <h3>{user}</h3>
      <p style={{ margin: 0 }}>{children}</p>
    </div>
  </div>
}

function EventsSidebar() {
  return <aside className={'events-aside'}>
    <nav>
      {events.map((event, key) =>
        <SidebarEventCard
          key={key}
          image={event.image}
          title={event.title}
          location={event.location}
          datetime={event.datetime}
          description={event.description}
          price={event.price}
        />
      )}
    </nav>
  </aside>
}

function SidebarEventCard({ image, title, location, datetime, description, price }: { image: string, title: string, location: string, datetime: Date, description: string, price: number|undefined }) {
  const month = datetime.toLocaleString('default', { month: 'short' }).toUpperCase();
  const day = datetime.getDate();
  const { data } = useColor(image, 'rgbArray', {crossOrigin: 'anonymous'});
  const [color, setColor] = useState<string>('hsl(209, 37%, 20%)');

  useEffect(() => {
    // If the colour is present and is a dark enough colour, use it
    if (data) {
      setColor(getDarkenedColor(data[0], data[1], data[2]));
    }
    else
      setColor('hsl(209, 37%, 20%)');
  }, [data]);

  return <article className={'event'}>
    <img src={image} alt={title} />
    <div>
      <div className={'date'}>
        <p style={{ color: color }}>{day}</p>
        <p style={{ color: color }}>{month}</p>
      </div>
      <div className={'info'}>
        <h2 style={{ color: color }}>{title}</h2>
        <p style={{ color: color }} className={'description'}>{description}</p>
        <div className={'location'}>
          <CiLocationOn />
          <p style={{ color: color }} className={''}>{location}</p>
        </div>
        <div className={'price'}>
          <CiShoppingTag />
          <p style={{ color: color }} className={''}>{price ? `£${price.toFixed(2).replace(/\.00$/, '')}` : 'Free entry'}</p>
        </div>
      </div>
    </div>
  </article>
}

function SocietiesSidebar() {
  const navigate = useNavigate();

  return <aside className={'societies-aside'}>
    <nav>
      {societies.map((society, key) =>
        <SidebarSocietyButton
          key={key}
          name={society.name}
          image={society.image} />
      )}
    </nav>
    <button className={'join-society-button'} onClick={() => navigate('/NavigateScreen')}>
      Join a Society
    </button>
  </aside>
}

function SidebarSocietyButton({ name, image }: { name: string, image: string }) {
  return <button className={'society-button'}>
    <img src={image} alt={`${name}'s society image`} />
    <div>
      <h3>{truncate(30, name)}</h3>
    </div>
  </button>
}

function truncate(length: number, str: string): string {
  if (str.length <= length)
    return str;

  return str.substring(0, length - 3) + '...';
}

function calculatePercievedBrightness(r: number, g: number, b: number): number {
  return 0.2126 * r + 0.7152 * g + 0.0722 * b;
}

function getDarkenedColor(r: number, g: number, b: number): string {
  const current = calculatePercievedBrightness(r, g, b);
  if (current <= 100)
    return `rgb(${r}, ${g}, ${b})`;

  const scaleFactor = 100 / current;

  // Scale each color component and clamp within the limits of rgb
  const newR = Math.max(0, Math.min(255, Math.floor(r * scaleFactor)));
  const newG = Math.max(0, Math.min(255, Math.floor(g * scaleFactor)));
  const newB = Math.max(0, Math.min(255, Math.floor(b * scaleFactor)));

  return `rgb(${newR}, ${newG}, ${newB})`;
}
