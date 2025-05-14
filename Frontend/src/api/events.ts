import axios from "axios";
import {BASE_URL} from "./defaults.ts";

export async function getEvents(id: number): Promise<EventResponse[]|undefined> {
    return axios.post(`${BASE_URL}/event/get`, {
        id
    })
        .then(response => {
            if (response.status == 200)
                return response.data as EventResponse[];
            return undefined;
        })
        .catch(() => {
            return undefined;
        });
}

export async function createEvent(societyId: number, token: string, expiry: Date, date: Date, location: string, name: string,
                                  description: string, price: number, image: string): Promise<boolean> {
    return axios.post(`${BASE_URL}/event/create`, {
        societyId, token, expiry, date, location, name, description, price, image
    })
        .then(response => response.status === 200)
        .catch(() => false);
}

export interface EventResponse {
    eventId: number,
    date: Date,
    location: string,
    name: string,
    description: string,
    price: number,
    image: string
}
