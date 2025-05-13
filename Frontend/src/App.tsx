import {Route, Routes} from "react-router-dom";
import Topbar from "./components/topbar.tsx";
import Home from "./pages/home.tsx";

function App() {
    return <div className={'flex flex-col h-full'}>
        <Topbar />
        <Routes>
            <Route path={'/'} element={<Home />} />
        </Routes>
    </div>
}

export default App
