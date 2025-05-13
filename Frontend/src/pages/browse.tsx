import {SocietyPane} from "../components/browse/societypane.tsx";
import {societies} from "../components/homepage/societypane.tsx";
import {useState} from "react";

export default function Browse() {
    const [currentSociety, setCurrentSociety] = useState(societies[0]);

    return <div className={'h-[calc(100%-112px)] w-full flex flex-row'}>
        <SocietyPane societies={societies} currentSociety={currentSociety} setCurrentSociety={setCurrentSociety} />
        <main className={'p-10'}>
            <h1 className={'text-3xl font-serif font-bold'}>About <span className={'text-blue-500'}>{currentSociety.societyName}</span></h1>
            <p>{currentSociety.societyDescription}</p>

            <button className={'mt-4 px-4 py-2 rounded-lg text-white bg-blue-500 hover:bg-blue-600 hover:cursor-pointer'}
                    onClick={() => alert(`You have joined ${currentSociety.societyName}!`)}>
                        Join {currentSociety.societyName}
            </button>
        </main>
    </div>
}
