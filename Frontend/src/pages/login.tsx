import {Link, useNavigate} from "react-router-dom";
import {type FormEvent, useContext, useState} from "react";
import {login} from "../api/login.ts";
import {UserContext} from "../context/UserContext.ts";

export default function Login() {
    const context = useContext(UserContext);
    const navigate = useNavigate();
    const [email, setEmail] = useState<string>("");
    const [password, setPassword] = useState<string>("");

    const handleSubmit = (e: FormEvent) => {
        e.preventDefault();

        login(email, password)
            .then(result => {
                if (result === undefined)
                    return;

                context.updateUser(result).then(() => navigate('/'));
            })
    }

    return (
        <div className="h-screen w-screen flex items-center justify-center bg-gray-100">
            <div className="bg-white p-10 rounded-lg shadow-lg w-[30rem] flex flex-col items-center">
                <img
                    src="/demoLogo.png"
                    alt="Logo"
                    className="w-32 h-32 mb-8"
                />
                <form className="w-full flex flex-col space-y-6" onSubmit={handleSubmit}>
                    <input
                        type="email"
                        placeholder="Email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        className="w-full p-4 border border-gray-300 rounded-lg text-lg"
                    />
                    <input
                        type="password"
                        placeholder="Password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        className="w-full p-4 border border-gray-300 rounded-lg text-lg"
                    />
                    <button
                        type="submit"
                        className="w-full p-4 bg-blue-500 text-white rounded-lg hover:bg-blue-600 hover:cursor-pointer text-lg"
                    >
                        Login
                    </button>
                </form>
                <Link
                    to="/signup"
                    className="mt-6 text-blue-500 hover:underline text-lg"
                >
                    Don't have an account? Sign up
                </Link>
            </div>
        </div>
    );
}
