import {useState} from "react";
import {societies, SocietyPane, type SocietyResponse} from "../components/homepage/societypane.tsx";
import {EventsPane} from "../components/homepage/eventpane.tsx";
import ChatPane from "../components/homepage/chatpane.tsx";

export default function Home() {
    // Todo: get societies based on user's joined societies
    const [currentSociety, setCurrentSociety] = useState<SocietyResponse>(societies[0]);
    const [isModalOpen, setIsModalOpen] = useState(false);

    return <div className={'h-full w-full flex flex-row bg-neutral-50'}>
        <SocietyPane societies={societies} currentSociety={currentSociety} setCurrentSociety={setCurrentSociety} />
        <ChatPane society={currentSociety} />
        <div className={'h-full flex flex-col items-center justify-between'}>
            <EventsPane society={currentSociety} />
            <button
                className={'px-4 mb-4 py-2 rounded-lg text-white bg-blue-500 hover:bg-blue-600 hover:cursor-pointer'}
                onClick={() => setIsModalOpen(true)}
            >
                Join a Society
            </button>
            {isModalOpen && <Modal onClose={() => setIsModalOpen(false)} />}
        </div>
    </div>
}

function Modal({ onClose }: { onClose: () => void }) {
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
                        <label className="block text-sm font-medium text-gray-700">Event Name</label>
                        <input
                            type="text"
                            placeholder="Enter event name"
                            className="w-full p-2 border border-gray-300 rounded-lg"
                            required
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Description</label>
                        <textarea
                            placeholder="Enter event description"
                            className="w-full p-2 border border-gray-300 rounded-lg"
                            required
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Date</label>
                        <input
                            type="date"
                            className="w-full p-2 border border-gray-300 rounded-lg hover:cursor-pointer"
                            required
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Location</label>
                        <input
                            type="text"
                            placeholder="Enter location"
                            className="w-full p-2 border border-gray-300 rounded-lg"
                            required
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Price</label>
                        <input
                            type="number"
                            placeholder="Enter price (default: 0)"
                            className="w-full p-2 border border-gray-300 rounded-lg"
                            defaultValue={0}
                            min={0}
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Image</label>
                        <input
                            type="file"
                            accept="image/*"
                            className="w-full p-2 border border-gray-300 rounded-lg hover:cursor-pointer"
                            required
                        />
                    </div>
                    <button
                        type="submit"
                        className="w-full p-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600"
                    >
                        Submit
                    </button>
                </form>
            </div>
        </div>
    );
}
