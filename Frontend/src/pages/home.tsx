import {useState} from "react";
import {societies, SocietyPane, type SocietyResponse} from "../components/homepage/societypane.tsx";
import {EventsPane} from "../components/homepage/eventpane.tsx";
import ChatPane from "../components/homepage/chatpane.tsx";

export default function Home() {
    // Todo: get societies based on user's joined societies
    const [currentSociety, setCurrentSociety] = useState<SocietyResponse>(societies[0]);

    return <div className={'h-full w-full flex flex-row bg-neutral-50'}>
        <SocietyPane societies={societies} currentSociety={currentSociety} setCurrentSociety={setCurrentSociety} />
        <ChatPane society={currentSociety} />
        <EventsPane society={currentSociety} />
    </div>
}
