import {createContext} from "react";
import type {LoginResponse} from "../api/login.ts";

export interface iUserContext {
    token: string | undefined,
    expiry: Date | undefined,
    username: string | undefined,
    email: string | undefined,
    profilePicture: string | undefined,
    universityId: number | undefined,
    universityLogo: string | undefined,
    universityTheming: iTheming | undefined,

    updateUser: (data: LoginResponse) => Promise<void>;
    tryLogin: () => Promise<boolean>;
    isLoggedIn: () => boolean;
    logOut: () => void;
}

export interface iTheming {
    primarycolor: string
}

export const UserContext = createContext<iUserContext>({} as iUserContext);
