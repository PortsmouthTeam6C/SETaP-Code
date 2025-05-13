import type {SocietyResponse} from "./societypane.tsx";

interface MessageResponse {
    messageId: number,
    userId: number,
    societyId: number,
    username: string,
    profilePicture: string,
    timestamp: Date,
    content: string
}

const messages: MessageResponse[] = [
    {
        messageId: 1,
        userId: 1,
        societyId: 1,
        username: "Alice",
        profilePicture: "https://images.pexels.com/photos/1239291/pexels-photo-1239291.jpeg",
        timestamp: new Date("2023-10-01T10:00:00Z"),
        content: "Looking forward to the next event!"
    },
    {
        messageId: 2,
        userId: 2,
        societyId: 1,
        username: "Bob",
        profilePicture: "https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg",
        timestamp: new Date("2023-10-01T10:05:00Z"),
        content: "Me too! The last one was amazing."
    },
    {
        messageId: 3,
        userId: 3,
        societyId: 1,
        username: "Charlie",
        profilePicture: "https://images.pexels.com/photos/415829/pexels-photo-415829.jpeg",
        timestamp: new Date("2023-10-01T10:10:00Z"),
        content: "Does anyone know the schedule for the next meeting?"
    },
    {
        messageId: 4,
        userId: 4,
        societyId: 1,
        username: "Diana",
        profilePicture: "https://images.pexels.com/photos/774909/pexels-photo-774909.jpeg",
        timestamp: new Date("2023-10-01T10:15:00Z"),
        content: "I think it’s on Friday at 5 PM. "
    },
    {
        messageId: 5,
        userId: 5,
        societyId: 1,
        username: "Eve",
        profilePicture: "https://images.pexels.com/photos/1130626/pexels-photo-1130626.jpeg",
        timestamp: new Date("2023-10-01T10:20:00Z"),
        content: "Can’t wait to see everyone there!"
    },
    {
        messageId: 6,
        userId: 6,
        societyId: 1,
        username: "Frank",
        profilePicture: "https://images.pexels.com/photos/91227/pexels-photo-91227.jpeg",
        timestamp: new Date("2023-10-01T10:25:00Z"),
        content: "I’ll bring some snacks for the meeting."
    }
];

export default function ChatPane({ society }: { society: SocietyResponse }) {
    // Todo: get messages based on society
    return <main className={'flex flex-col h-full w-[calc(100%-33rem)] p-10 space-y-4 justify-between'}>
        <div className={'flex flex-col space-y-4'}>
            {messages.map((message, i) => <ChatMessage key={i} message={message} />)}
        </div>
        <div className={'flex items-center space-x-4 -m-6'}>
            <input className={'flex-grow p-2 border border-gray-300 rounded-lg'}
                type="text"
                placeholder="Type your message..." />
            <button className={'px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 hover:cursor-pointer'}>
                Send
            </button>
        </div>
    </main>
}

function ChatMessage({ message }: { message: MessageResponse }) {
    const { username, profilePicture, timestamp, content } = message;
    return <article className={'w-full space-x-3 flex'}>
        <img className={'h-16 aspect-square object-cover rounded-full'}
             src={profilePicture}
             alt={username} />
        <div className={'flex grow flex-col'}>
            <div className={'flex space-x-4'}>
                <h2 className={'font-bold text-xl'}>{username}</h2>
                <p className={'text-xl'}>{timestamp.toLocaleString()}</p>
            </div>
            <p>{content}</p>
        </div>
    </article>
}
