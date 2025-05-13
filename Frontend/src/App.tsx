import {Route, Routes} from "react-router-dom";
import Topbar from "./components/topbar.tsx";
import Home from "./pages/home.tsx";
import Browse from "./pages/browse.tsx";
import Login from "./pages/login.tsx";
import Signup from "./pages/signup.tsx";

function App() {
    return <div className={'flex flex-col h-full'}>
        <Topbar />
        <Routes>
            <Route path={'/'} element={<Home />} />
            <Route path={'/browse'} element={<Browse />} />
            <Route path={'/login'} element={<Login />} />
            <Route path={'/signup'} element={<Signup />} />
        </Routes>
    </div>
}

export default App
