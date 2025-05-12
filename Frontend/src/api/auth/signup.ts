import axios from "axios";
import {BASE_URL} from "../defaults";
import {LoginResponse} from "./login";

export async function reserveAccount(username: string, email: string, password: string): Promise<(string) => LoginResponse> {
    const reserveResponse = await axios.post(`${BASE_URL}/user/signup/reserve-account`, {
        username,
        email,
        password
    });

    return async (verificationCode: string) => {
        const verifyResponse = await axios.post(`${BASE_URL}/user/signup/verify-account`, {
            accountIdentifier: reserveResponse.data.identityToken,
            verificationCode
        });
        return verifyResponse.data as LoginResponse;
    }
}
