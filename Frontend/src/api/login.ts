import {BASE_URL} from "./defaults.ts";
import axios from 'axios';

export async function login(email: string, password: string): Promise<LoginResponse|undefined> {
    return axios.post(`${BASE_URL}/account/login/email`, {
        email,
        password
    })
        .then(response => {
            if (response.status == 200)
                return response.data as LoginResponse;
            return undefined;
        })
        .catch(() => {
            return undefined;
        });
}

export async function tokenLogin(token: string, expiry: Date): Promise<LoginResponse|undefined> {
    return axios.post(`${BASE_URL}/account/login/token`, {
        token,
        expiry
    })
        .then(response => {
            if (response.status == 200)
                return response.data as LoginResponse;
            return undefined;
        })
        .catch(() => {
            return undefined;
        });
}

export async function signup(email: string, username: string, password: string): Promise<LoginResponse|undefined> {
    return axios.post(`${BASE_URL}/account/create`, {
        email,
        username,
        password
    })
        .then(response => {
            if (response.status == 200)
                return response.data as LoginResponse;
            return undefined;
        })
        .catch(() => {
            return undefined;
        });
}

export interface LoginResponse {
    token: string,
    expiry: Date,
    username: string,
    email: string,
    profilePicture: string,
    universityId: number,
    universityPicture: string,
    theming: string
}
