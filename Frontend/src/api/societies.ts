import axios from "axios";
import {BASE_URL} from "./defaults.ts";

export async function getJoinedSocieties(token: string, expiry: Date): Promise<SocietyResponse[]|undefined> {
    return axios.post(`${BASE_URL}/society/get/joined`, {
        token,
        expiry,
    })
        .then(response => {
            if (response.status == 200)
                return response.data as SocietyResponse[];
            return undefined;
        })
        .catch(() => {
            return undefined;
        });
}

export async function getAllSocieties(id: number) {
    return axios.post(`${BASE_URL}/society/get/all`, {
        id
    })
        .then(response => {
            if (response.status == 200)
                return response.data as SocietyResponse[];
            return undefined;
        })
        .catch(() => {
            return undefined;
        });
}

export async function joinSociety(token: string, expiry: Date, id: number): Promise<boolean> {
    return axios.post(`${BASE_URL}/society/join`, {
        token,
        expiry,
        id
    })
        .then(response => response.status == 200)
        .catch(() => {
            return false;
        });
}

export interface SocietyResponse {
    societyId: number,
    universityId: number,
    societyName: string,
    societyDescription: string,
    societyPicture: string
}
