import type {SetState} from "../../utils/types.ts";
import {useNavigate} from "react-router-dom";

export interface SocietyResponse {
    societyId: number,
    universityId: number,
    societyName: string,
    societyDescription: string,
    societyPicture: string,
}

export const societies: SocietyResponse[] = [
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
