import axios from "axios";

export async function getPublicKeyOfMLKEM() {
    const { data } = await axios.get<string>("/encrypt-decrypt/ml-kem/public-key");
    return data;
}