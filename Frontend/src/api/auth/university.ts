import axios from "axios";
import {BASE_URL} from "../defaults";

export async function getUniversityFromUser(token: string, expiry: Date) {
    return axios.post(`${BASE_URL}/university/getFromUser`, {
        token,
        expiry
    })
        .then(response => {
            if (response.status == 200)
                return response.data as UniversityResponse;
            return undefined;
        })
        .catch(() => undefined);
}

export interface UniversityResponse {
    universityName: string,
    universityTheming: string
}