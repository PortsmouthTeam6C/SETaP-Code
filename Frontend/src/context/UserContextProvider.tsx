import {ReactNode, useState} from "react";
import {iSettings, iTheming, UserContext} from "./UserContext";
import {LoginResponse, tokenLogin} from "../api/auth/login";
import Cookies from "universal-cookie";

export default function UserContextProvider({ children }: { children: ReactNode }) {
    const [token, setToken] = useState<string | undefined>(undefined);
    const [expiry, setExpiry] = useState<Date | undefined>(undefined);
    const [username, setUsername] = useState<string | undefined>(undefined);
    const [email, setEmail] = useState<string | undefined>(undefined);
    const [profilePicture, setProfilePicture] = useState<string | undefined>(undefined);
    const [isAdministrator, setIsAdministrator] = useState<boolean | undefined>(undefined);
    const [settings, setSettings] = useState<iSettings | undefined>(undefined);
    const [universityName, setUniversityName] = useState<string | undefined>(undefined);
    const [universityLogo, setUniversityLogo] = useState<string | undefined>(undefined);
    const [universityTheming, setUniversityTheming] = useState<iTheming | undefined>(undefined);

    return <UserContext.Provider value={{
        token,
        expiry,
        username,
        email,
        profilePicture,
        isAdministrator,
        settings,
        universityName,
        universityLogo,
        universityTheming,

        updateUser: async (data: LoginResponse) => {
            // Set user-related data
            setUsername(data.username);
            setEmail(data.email);
            setProfilePicture(data.profilePicture);
            setIsAdministrator(data.isAdministrator);
            setSettings(JSON.parse(data.settings));

            // Set university-related data
            setUniversityName(data.universityName);
            setUniversityLogo(data.universityLogo);
            setUniversityTheming(JSON.parse(data.universityTheming));

            // Store the tokens to keep the player logged in
            if (data.token && data.expiry) {
                setToken(data.token);
                setExpiry(data.expiry);

                const cookies = new Cookies();
                const expiryDate = new Date();
                expiryDate.setDate(expiryDate.getDate() + 20);
                console.log("saving ", data.token, data.expiry)
                cookies.set('auth-token', data.token, { path: '/', expires: expiryDate, secure: false })
                cookies.set('auth-expiry', data.expiry, { path: '/', expires: expiryDate, secure: false })
            }
        },

        tryLogin: async () => {
            // Retrieve the cookies containing token information
            const cookies = new Cookies();
            const authToken = cookies.get('auth-token');
            const authExpiry = cookies.get('auth-expiry');

            console.log("loading ", authToken, authExpiry)

            // If there are no saved tokens, autologin failed
            if (authToken === 'undefined' || authExpiry === 'undefined')
                return false;

            const response = await tokenLogin(authToken, authExpiry);

            // If the response is undefined, autologin failed
            if (!response)
                return false;

            setToken(authToken);
            setExpiry(authExpiry);

            // Set user-related data
            setUsername(response.username);
            setEmail(response.email);
            setProfilePicture(response.profilePicture);
            setIsAdministrator(response.isAdministrator);
            setSettings(JSON.parse(response.settings));

            // Set university-related data
            setUniversityName(response.universityName);
            setUniversityLogo(response.universityLogo);
            setUniversityTheming(JSON.parse(response.universityTheming));

            return true;
        }
    }}>{children}</UserContext.Provider>
}
