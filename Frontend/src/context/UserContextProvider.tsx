import {type ReactNode, useState} from "react";
import {type iTheming, UserContext} from "./UserContext.ts";
import Cookies from "universal-cookie";
import {type LoginResponse, tokenLogin} from "../api/login.ts";

export default function UserContextProvider({ children }: { children: ReactNode }) {
    const [token, setToken] = useState<string | undefined>(undefined);
    const [expiry, setExpiry] = useState<Date | undefined>(undefined);
    const [username, setUsername] = useState<string | undefined>(undefined);
    const [email, setEmail] = useState<string | undefined>(undefined);
    const [profilePicture, setProfilePicture] = useState<string | undefined>(undefined);
    const [universityId, setUniversityId] = useState<number | undefined>(undefined);
    const [universityLogo, setUniversityLogo] = useState<string | undefined>(undefined);
    const [universityTheming, setUniversityTheming] = useState<iTheming | undefined>(undefined);

    return <UserContext.Provider value={{
        token,
        expiry,
        username,
        email,
        profilePicture,
        universityId,
        universityLogo,
        universityTheming,

        isLoggedIn: () => token !== undefined,

        updateUser: async (data: LoginResponse) => {
            // Set user-related data
            setUsername(data.username);
            setEmail(data.email);
            setProfilePicture(data.profilePicture);

            // Set university-related data
            setUniversityId(data.universityId);
            setUniversityLogo(data.universityPicture);
            setUniversityTheming(JSON.parse(data.theming));

            // Store the tokens to keep the player logged in
            if (data.token && data.expiry) {
                setToken(data.token);
                setExpiry(data.expiry);

                const cookies = new Cookies();
                const expiryDate = new Date();
                expiryDate.setDate(expiryDate.getDate() + 20);
                cookies.set('auth-token', data.token, { path: '/', expires: expiryDate, secure: false })
                cookies.set('auth-expiry', data.expiry, { path: '/', expires: expiryDate, secure: false })
            }
        },

        tryLogin: async () => {
            // Retrieve the cookies containing token information
            const cookies = new Cookies();
            const authToken = cookies.get('auth-token');
            const authExpiry = cookies.get('auth-expiry');

            // If there are no saved tokens, autologin failed
            if (authToken === undefined || authExpiry === undefined)
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

            // Set university-related data
            setUniversityId(response.universityId);
            setUniversityLogo(response.universityPicture);
            setUniversityTheming(JSON.parse(response.theming));

            return true;
        },

        logOut: () => {
            const cookies = new Cookies();
            cookies.remove('auth-token');
            cookies.remove('auth-expiry');

            // Purge the settings
            setToken(undefined);
            setExpiry(undefined);
            setUsername(undefined);
            setEmail(undefined);
            setProfilePicture(undefined);
            setUniversityId(undefined);
            setUniversityLogo(undefined);
            setUniversityTheming(undefined);

        }
    }}>{children}</UserContext.Provider>
}
