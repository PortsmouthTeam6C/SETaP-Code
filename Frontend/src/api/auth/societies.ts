import axios from "axios";
import {BASE_URL} from "../defaults";

export async function getJoinedSocieties(token: string, expiry: Date): Promise<SocietyResponse[]|undefined> {
  return axios.post(`${BASE_URL}/societies/joined`, {
    token,
    expiry
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

export interface SocietyResponse {
  id: number,
  name: string,
  description: string,
  picture: string,
  maxSize: number,
  isPaid: boolean
}
