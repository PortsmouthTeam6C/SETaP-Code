import type {SocietyResponse} from "../../api/societies.ts";
import {getMessages, type MessageResponse, sendMessage} from "../../api/messages.ts";
import {useContext, useEffect, useState} from "react";
import {UserContext} from "../../context/UserContext.ts";

export default function ChatPane({ society }: { society: SocietyResponse }) {
    const context = useContext(UserContext);
    const [messages, setMessages] = useState<MessageResponse[]>([]);
    const [message, setMessage] = useState<string>("");

    const refreshMessages = () => {
        getMessages(society.societyId)
            .then(response => {
                if (response === undefined)
                    setMessages([]);
                else
                    setMessages(response);
            })
    }

    useEffect(refreshMessages, [society]);

    const onSend = () => {
        // Don't try to send a message if the user isn't logged in or the message is blank
        if (!context.isLoggedIn() || message == "")
            return;

        sendMessage(context.token!, context.expiry!, society.societyId, message)
            .then(success => success && refreshMessages());
    }

    return <main className={'flex flex-col h-full w-[calc(100%-33rem)] p-10 space-y-4 justify-between'}>
        <div className={'flex flex-col space-y-4'}>
            {messages.map((message, i) => <ChatMessage key={i} message={message} />)}
        </div>
        <div className={'flex items-center space-x-4 -m-6'}>
            <input className={'flex-grow p-2 border border-gray-300 rounded-lg'}
                   type="text"
                   placeholder="Type your message..."
                   value={message}
                   onChange={(e) => setMessage(e.target.value)}
            />
            <button className={'px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 hover:cursor-pointer'}
                    onClick={onSend}>
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
                <p className={'text-xl'}>{new Date(timestamp).toLocaleString()}</p>
            </div>
            <p>{content}</p>
        </div>
    </article>
}
