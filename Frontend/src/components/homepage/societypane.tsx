import type {SetState} from "../../utils/types.ts";
import {useNavigate} from "react-router-dom";
import type {SocietyResponse} from "../../api/societies.ts";

function SocietyButton({ society, onClick }: { society: SocietyResponse, onClick: () => void }) {
    return <button className={'flex flex-row space-x-2'} onClick={onClick}>
        <img className={'h-full aspect-square object-cover m-2 rounded-2xl'}
             src={society.societyPicture}
             alt={society.societyName} />
    </button>
}

export function SocietyPane({ societies, currentSociety, setCurrentSociety }: { societies: SocietyResponse[], currentSociety: SocietyResponse, setCurrentSociety: SetState<SocietyResponse> }) {
    const navigate = useNavigate();

    return <aside className={'bg-neutral-100 h-full min-w-36 w-36 flex flex-col items-center justify-between'}>
        <div className={'w-full space-y-2 [&>*]:h-32 relative'}>
            <div className='absolute top-2 w-1 bg-black transition-all' style={{
                transform: `translate(0px, ${136 * Math.max(societies.indexOf(currentSociety), 0)}px)`
            }} />
            {societies.map((society, i) => <SocietyButton
                key={i}
                society={society}
                onClick={() => setCurrentSociety(society)}
            />)}
        </div>
        <button
            className={'px-4 mb-4 py-2 rounded-lg text-white bg-blue-500 hover:bg-blue-600 hover:cursor-pointer'}
            onClick={() => navigate('/browse')}
        >
            Join a Society
        </button>
    </aside>
}
