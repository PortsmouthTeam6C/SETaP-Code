import {useLocation, useNavigate} from "react-router-dom";
import {useContext, useEffect, useState} from "react";
import {UserContext} from "../context/UserContext.ts";

export default function Topbar() {
    const context = useContext(UserContext);
    const location = useLocation();
    const navigate = useNavigate();

    const [logo, setLogo] = useState<string>("");
    const [username, setUsername] = useState<string>("");
    const [profilePicture, setProfilePicture] = useState<string>("");
    useEffect(() => {
        if (context.isLoggedIn()) {
            setLogo(context.universityLogo!);
            setUsername(context.username!);
            setProfilePicture(context.profilePicture!);
            return;
        }
        setLogo("");
        setUsername("");
        setProfilePicture("");

        context.tryLogin().then(success => {
            if (success && (location.pathname === '/login' || location.pathname === '/signup'))
                navigate('/');
            if (!success && (location.pathname === '/' || location.pathname === '/browse'))
                navigate('/login');
        });
    }, [context]);

    if (location.pathname === '/login' || location.pathname === '/signup')
        return <></>

    return <header className={'w-full min-h-28 max-h-28 bg-neutral-400 flex justify-between'}>
        <img className={'h-full'}
             src={logo}
             alt={username}/>
        <div className={'flex items-center space-x-4 justify-end pr-4 relative group'}>
            <p className={'text-3xl'}>{username}</p>
            <img className={'h-9/12 aspect-square rounded-full object-cover'}
                 src={profilePicture}
                 alt={username}/>
            <div className={'absolute mt-2 w-32 bg-white border border-gray-300 rounded-lg shadow-lg hidden group-hover:block z-50 top-24 right-3'}>
                <button className={'w-full px-4 py-2 text-left hover:bg-gray-100 hover:cursor-pointer'} onClick={context.logOut}>
                    Sign Out
                </button>
            </div>
        </div>
    </header>
}
