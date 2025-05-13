import { Link } from "react-router-dom";

export default function Login() {
    // Todo: add login functionality
    return (
        <div className="h-screen w-screen flex items-center justify-center bg-gray-100">
            <div className="bg-white p-10 rounded-lg shadow-lg w-[30rem] flex flex-col items-center">
                <img
                    src="/demoLogo.png"
                    alt="Logo"
                    className="w-32 h-32 mb-8"
                />
                <form className="w-full flex flex-col space-y-6">
                    <input
                        type="email"
                        placeholder="Email"
                        className="w-full p-4 border border-gray-300 rounded-lg text-lg"
                    />
                    <input
                        type="password"
                        placeholder="Password"
                        className="w-full p-4 border border-gray-300 rounded-lg text-lg"
                    />
                    <button
                        type="submit"
                        className="w-full p-4 bg-blue-500 text-white rounded-lg hover:bg-blue-600 text-lg"
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