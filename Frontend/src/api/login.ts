import axios from 'axios';

export async function login(email: string, password: string): Promise<LoginResponse> {
    const response = await axios.post('http://localhost:7071/login', {
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
