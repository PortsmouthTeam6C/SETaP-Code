import type {SetState} from "../../utils/types.ts";
import truncate from "../../utils/truncate.ts";
import {useState} from "react";
import type {SocietyResponse} from "../../api/societies.ts";

function SocietyButton({ society, onClick }: { society: SocietyResponse, onClick: () => void }) {
    return <button className={'bg-neutral-50 w-full h-96 shadow-lg rounded-2xl hover:cursor-pointer relative'}
                   onClick={onClick}>
        <img className={'h-1/2 w-full object-cover shadow-lg rounded-2xl'}
             src={society.societyPicture}
             alt={society.societyName} />
        <div className={'h-1/2 w-full flex flex-col justify-center items-center p-3'}>
            <h3 className={'w-full text-3xl font-serif'}>{society.societyName}</h3>
            <p className={'w-full grow text-left'}>{truncate(230, society.societyDescription)}</p>
        </div>
        {/* Used to provide a translucent black overlay on hover */}
        <div className={'absolute top-0 h-full w-full hover:bg-black/20 transition-all rounded-2xl'} />
    </button>
}

export function SocietyPane({ societies, currentSociety, setCurrentSociety }: { societies: SocietyResponse[], currentSociety: SocietyResponse, setCurrentSociety: SetState<SocietyResponse|undefined> }) {
    const [searchQuery, setSearchQuery] = useState("");
    const filteredSocieties = societies.filter(society =>
        society.societyName.toLowerCase().includes(searchQuery.toLowerCase())
    );

    return <aside className={'bg-neutral-100 h-full min-w-96 w-96 max-w-96 flex flex-col items-center space-y-2 overflow-scroll p-2'}>
        <input
            type="text"
            placeholder="Search societies..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className={'w-full p-2 mb-4 border border-gray-300 rounded-lg'}
        />
        <div className='absolute top-2 w-1 bg-black transition-all' style={{
            transform: `translate(0px, ${136 * Math.max(societies.indexOf(currentSociety), 0)}px)`
        }} />
        {filteredSocieties.map((society, i) => <SocietyButton
            key={i}
            society={society}
            onClick={() => setCurrentSociety(society)}
        />)}
    </aside>
}
