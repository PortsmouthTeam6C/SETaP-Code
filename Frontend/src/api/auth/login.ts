import axios from 'axios';
import {BASE_URL} from "./defaults";

export async function login(email: string, password: string): Promise<LoginResponse> {
const response = await axios.post(`${BASE_URL}/user/login`, {
        email,
        password
    });
    return response.data as LoginResponse;
}

export interface LoginResponse {
    token: string,
    expiry: Date,
    username: string,
    email: string,
    profilePicture: string,
    isAdministrator: boolean,
    settings: string
}
