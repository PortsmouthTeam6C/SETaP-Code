import axios from 'axios';
import {BASE_URL} from "../defaults";

export async function login(email: string, password: string): Promise<LoginResponse|undefined> {
    return axios.post(`${BASE_URL}/user/login`, {
        email,
        password
    })
        .then(response => {
          console.log(response);
            if (response.status == 200)
                return response.data as LoginResponse;
            return undefined;
        })
        .catch(() => {
            return undefined;
        });
}

export async function tokenLogin(token: string, expiry: Date): Promise<LoginResponse|undefined> {
    return axios.post(`${BASE_URL}/user/get`, {
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

export interface LoginResponse {
    token: string,
    expiry: Date,
    username: string,
    email: string,
    profilePicture: string,
    isAdministrator: boolean,
    settings: string,
    universityName: string,
    universityLogo: string,
    universityTheming: string
}
