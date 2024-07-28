import { Outlet, NavLink } from "react-router-dom";

import Navbar from "../Navbar";

export default function RootLayout() {
    return (
        <div className="root-layout">
            <Navbar/>
            <main>
                <Outlet/>
            </main>
        </div>
    )
}