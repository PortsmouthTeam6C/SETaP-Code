import {createContext} from "react";
import {LoginResponse} from "../api/auth/login";

export interface iUserContext {
    token: string | undefined,
    expiry: Date | undefined,
    username: string | undefined,
    email: string | undefined,
    profilePicture: string | undefined,
    isAdministrator: boolean | undefined,
    settings: iSettings | undefined,
    universityName: string | undefined,
    universityLogo: string | undefined,
    universityTheming: iTheming | undefined,

    updateUser: (data: LoginResponse) => Promise<void>;
    tryLogin: () => Promise<boolean>;
}

export interface iSettings {

}

export interface iTheming {

}

export const UserContext = createContext<iUserContext>({} as iUserContext);
