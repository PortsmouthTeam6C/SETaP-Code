import axios from "axios";
import {BASE_URL} from "./defaults.ts";

export async function getMessages(id: number): Promise<MessageResponse[]|undefined> {
    return axios.post(`${BASE_URL}/chat/get`, {
        id
    })
        .then(response => {
            if (response.status == 200)
                return response.data as MessageResponse[];
            return undefined;
        })
        .catch(() => {
            return undefined;
        });
}

export async function sendMessage(token: string, expiry: Date, societyId: number, content: string): Promise<boolean> {
    return axios.post(`${BASE_URL}/chat/send`, {
        token, expiry, societyId, content
    })
        .then(response => response.status === 200)
        .catch(() => false);
}

export interface MessageResponse {
    messageId: number,
    userId: number,
    societyId: number,
    username: string,
    profilePicture: string,
    timestamp: Date,
    content: string
}
