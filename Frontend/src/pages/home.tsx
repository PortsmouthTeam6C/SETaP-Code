import {type FormEvent, useContext, useEffect, useState} from "react";
import {SocietyPane} from "../components/homepage/societypane.tsx";
import {EventsPane} from "../components/homepage/eventpane.tsx";
import ChatPane from "../components/homepage/chatpane.tsx";
import {UserContext} from "../context/UserContext.ts";
import {getJoinedSocieties, type SocietyResponse} from "../api/societies.ts";
import {createEvent} from "../api/events.ts";

export default function Home() {
    const context = useContext(UserContext);
    const [societies, setSocieties] = useState<SocietyResponse[]>([]);
    const [currentSociety, setCurrentSociety] = useState<SocietyResponse>(societies[0]);
    const [isModalOpen, setIsModalOpen] = useState(false);

    useEffect(() => {
        if (context.isLoggedIn()) {
            getJoinedSocieties(context.token!, context.expiry!)
                .then(response => {
                    if (response === undefined) {
                        setSocieties([]);
                        setCurrentSociety([][0])
                    }
                    else {
                        setSocieties(response);
                        setCurrentSociety(response[0])
                    }
                })
        }
    }, [context]);

    return currentSociety && <div className={'h-full w-full flex flex-row bg-neutral-50'}>
        <SocietyPane societies={societies} currentSociety={currentSociety} setCurrentSociety={setCurrentSociety} />
        <ChatPane society={currentSociety} />
        <div className={'h-full flex flex-col items-center justify-between'}>
            <EventsPane society={currentSociety} />
            <button
                className={'px-4 mb-4 py-2 rounded-lg text-white bg-blue-500 hover:bg-blue-600 hover:cursor-pointer'}
                onClick={() => setIsModalOpen(true)}
            >
                Create an Event
            </button>
            {isModalOpen && <Modal currentSociety={currentSociety} onClose={() => setIsModalOpen(false)} />}
        </div>
    </div>
}

function fileToBase64(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = () => resolve(reader.result as string);
        reader.onerror = (error) => reject(error);
        reader.readAsDataURL(file);
    });
}

function Modal({ currentSociety, onClose }: { currentSociety: SocietyResponse, onClose: () => void }) {
    const context = useContext(UserContext);
    const [eventName, setEventName] = useState<string>('');     // default to empty
    const [description, setDescription] = useState<string>(''); // default to empty
    const [date, setDate] = useState<string>('');               // default to empty
    const [location, setLocation] = useState<string>('');       // default to empty
    const [price, setPrice] = useState<number>(0);              // default to 0
    const [image, setImage] = useState<File | null>(null);      // allow null safely


    const handleSubmit = (e: FormEvent) => {
        e.preventDefault();
        if (!context.isLoggedIn())
            return;

        fileToBase64(image!)
            .then(async base64Image => {
                await createEvent(currentSociety.societyId, context.token!, context.expiry!,
                    new Date(date!), location!, eventName!, description!, price!, base64Image);
                window.location.reload();
            })
            .catch(error => console.log("Error converting file to base64: ", error));
    }
    
    return (
        <div
            className="fixed inset-0 bg-black/20 bg-opacity-50 flex items-center justify-center"
            onClick={onClose}
        >
            <div
                className="bg-white p-6 rounded-lg shadow-lg relative"
                onClick={(e) => e.stopPropagation()} // Prevent closing when clicking inside the modal
            >
                <button
                    className="absolute top-2 right-2 text-gray-500 hover:text-gray-700 hover:cursor-pointer"
                    onClick={onClose}
                >
                    X
                </button>
                <h2 className="text-xl font-bold mb-4">Create Event</h2>
                <form className="space-y-4">
                    <div>
                        <label htmlFor="eventName" className="block text-sm font-medium text-gray-700">Event Name</label>
                        <input
                            id="eventName"
                            type="text"
                            placeholder="Enter event name"
                            className="w-full p-2 border border-gray-300 rounded-lg"
                            required
                            value={eventName}
                            onChange={e => setEventName(e.target.value)}
                        />
                    </div>
                    <div>
                        <label htmlFor="description" className="block text-sm font-medium text-gray-700">Description</label>
                        <textarea
                            id="description"
                            placeholder="Enter event description"
                            className="w-full p-2 border border-gray-300 rounded-lg"
                            required
                            value={description}
                            onChange={e => setDescription(e.target.value)}
                        />
                    </div>
                    <div>
                        <label htmlFor="eventDate" className="block text-sm font-medium text-gray-700">Date</label>
                        <input
                            id="eventDate"
                            type="date"
                            className="w-full p-2 border border-gray-300 rounded-lg hover:cursor-pointer"
                            required
                            value={date}
                            onChange={e => setDate(e.target.value)}
                        />
                    </div>
                    <div>
                        <label htmlFor="location" className="block text-sm font-medium text-gray-700">Location</label>
                        <input
                            id="location"
                            type="text"
                            placeholder="Enter location"
                            className="w-full p-2 border border-gray-300 rounded-lg"
                            required
                            value={location}
                            onChange={e => setLocation(e.target.value)}
                        />
                    </div>
                    <div>
                        <label htmlFor="price" className="block text-sm font-medium text-gray-700">Price</label>
                        <input
                            id="price"
                            type="number"
                            placeholder="Enter price (default: 0)"
                            className="w-full p-2 border border-gray-300 rounded-lg"
                            min={0}
                            value={price}
                            onChange={e => setPrice(parseFloat(e.target.value))}
                        />
                    </div>
                    <div>
                        <label htmlFor="imageUpload" className="block text-sm font-medium text-gray-700">Image</label>
                        <input
                            id="imageUpload"
                            type="file"
                            accept="image/*"
                            className="w-full p-2 border border-gray-300 rounded-lg hover:cursor-pointer"
                            required
                            onChange={(e) => setImage(e.target.files?.[0] ?? null)}
                        />
                    </div>
                    <button
                        type="submit"
                        className="w-full p-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 hover:cursor-pointer"
                        onClick={handleSubmit}
                    >
                        Submit
                    </button>
                </form>
            </div>
        </div>
    );
}
