import {useState} from "react";
import {SocietyPane} from "../components/societypane.tsx";
import {EventsPane} from "../components/eventpane.tsx";
import ChatPane from "../components/chatpane.tsx";

export interface SocietyResponse {
    societyId: number,
    universityId: number,
    societyName: string,
    societyDescription: string,
    societyPicture: string
}

const societies: SocietyResponse[] = [
    {
        societyId: 1,
        universityId: 1,
        societyName: "Chess Club",
        societyDescription: "Join us for strategic gameplay and tournaments",
        societyPicture: "https://images.pexels.com/photos/6115019/pexels-photo-6115019.jpeg"
    },
    {
        societyId: 2,
        universityId: 1,
        societyName: "Debate Society",
        societyDescription: "Enhance your public speaking and critical thinking",
        societyPicture: "https://images.pexels.com/photos/6077797/pexels-photo-6077797.jpeg"
    },
    {
        societyId: 3,
        universityId: 1,
        societyName: "Photography Club",
        societyDescription: "Capture moments and learn photography techniques",
        societyPicture: "https://images.pexels.com/photos/212372/pexels-photo-212372.jpeg"
    },
    {
        societyId: 4,
        universityId: 1,
        societyName: "Drama Club",
        societyDescription: "Express yourself through theatrical performances",
        societyPicture: "https://images.pexels.com/photos/713149/pexels-photo-713149.jpeg"
    },
    {
        societyId: 5,
        universityId: 1,
        societyName: "Science Society",
        societyDescription: "Explore scientific discoveries and experiments",
        societyPicture: "https://images.pexels.com/photos/256262/pexels-photo-256262.jpeg"
    }
]

export default function Home() {
    // Todo: get societies based on user's joined societies
    const [currentSociety, setCurrentSociety] = useState<SocietyResponse>(societies[0]);

    return <div className={'h-full w-full flex flex-row bg-neutral-50'}>
        <SocietyPane societies={societies} currentSociety={currentSociety} setCurrentSociety={setCurrentSociety} />
        <ChatPane society={currentSociety} />
        <EventsPane society={currentSociety} />
    </div>
}
