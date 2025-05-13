import {useLocation} from "react-router-dom";

export default function Topbar() {
    const location = useLocation();
    if (location.pathname === '/login' || location.pathname === '/signup')
        return <></>

    const universityLogo: string = "https://upload.wikimedia.org/wikipedia/commons/d/dc/University_of_Portsmouth_Logo.png";
    const username: string = "johndoe";
    const profilePicture: string = "https://images.pexels.com/photos/10513822/pexels-photo-10513822.jpeg";

    return <header className={'w-full min-h-28 max-h-28 bg-neutral-400 flex justify-between'}>
        <img className={'h-full'}
             src={universityLogo}
             alt={username}/>
        <div className={'flex items-center space-x-4 justify-end pr-4'}>
            <p className={'text-3xl'}>{username}</p>
            <img className={'h-9/12 aspect-square rounded-full object-cover'}
                 src={profilePicture}
                 alt={username}/>
        </div>
    </header>
}
