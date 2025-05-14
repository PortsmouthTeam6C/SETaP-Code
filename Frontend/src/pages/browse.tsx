import {SocietyPane} from "../components/browse/societypane.tsx";
import {useContext, useEffect, useState} from "react";
import {getAllSocieties, joinSociety, type SocietyResponse} from "../api/societies.ts";
import {UserContext} from "../context/UserContext.ts";

export default function Browse() {
    const context = useContext(UserContext);
    const [societies, setSocieties] = useState<SocietyResponse[]>([])
    const [currentSociety, setCurrentSociety] = useState<SocietyResponse|undefined>(societies[0]);

    useEffect(() => {
        if (context.isLoggedIn()) {
            getAllSocieties(context.universityId!)
                .then(response => {
                    if (response === undefined) {
                        setSocieties([]);
                        setCurrentSociety(undefined);
                    } else {
                        setSocieties(response);
                        setCurrentSociety(response[0]);
                    }
                })
        }
    }, [context]);

    const onClick = () => {
        if (context.isLoggedIn() && currentSociety !== undefined) {
            joinSociety(context.token!, context.expiry!, currentSociety.societyId)
                .then(success => {
                    if (success)
                        alert("Successfully joined society.");
                    else
                        alert("Joining society was unsuccessful, please try again later.")
                });
        }
    }

    return currentSociety && <div className={'h-[calc(100%-112px)] w-full flex flex-row'}>
        <SocietyPane societies={societies} currentSociety={currentSociety} setCurrentSociety={setCurrentSociety} />
        <main className={'p-10'}>
            <h1 className={'text-3xl font-serif font-bold'}>About <span className={'text-blue-500'}>{currentSociety.societyName}</span></h1>
            <p>{currentSociety.societyDescription}</p>

            <button className={'mt-4 px-4 py-2 rounded-lg text-white bg-blue-500 hover:bg-blue-600 hover:cursor-pointer'}
                    onClick={onClick}>
                        Join {currentSociety.societyName}
            </button>
        </main>
    </div>
}
