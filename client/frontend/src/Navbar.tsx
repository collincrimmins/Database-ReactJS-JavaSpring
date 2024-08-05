import React, {useState, useEffect} from 'react'
import { Route, NavLink, Link, Routes, useLocation, useNavigate } from 'react-router-dom';

import "./css/App.css"
import "./css/Navbar.css"

import reactLogo from './images/react.svg'
import { useAuthContext } from './contexts/useAuthContext';

export default function Navbar() {
    const {user, logoutUser} = useAuthContext()

    const location = useLocation();
    const navigate = useNavigate()
    
    // Button that will be Underlined when Page Active
    type ViewPageButtonProps = {
        dest : string,
        children: React.ReactNode
    }
    function ViewPageButton({dest, children} : ViewPageButtonProps) {
        // Check Page Active
        let pageActive = false
        if (dest == "/login") {
            if (location.pathname.includes("login") || location.pathname.includes("register")) {
                pageActive = true
            }
        }
        if (dest == "/students") {
            if (location.pathname.includes("students")) {
                pageActive = true
            }
        }
        
        // Mark Link as Active or not Active
        return (
            <NavLink to={dest} className={`NavbarItem ${pageActive ? "NavbarItemActive" : ""}`}> {children} </NavLink>
            // <Link href={dest} className={`NavbarItem ${pageActive ? "NavbarItemActive" : ""}`}> {children} </Link>
        )
    }

    function LoginButtonClick() {
        navigate("/login")
    }

    function LogoutButtonClick() {
        logoutUser()
    }

    function LoginButton() {
        return (
            <div className="NavbarItem LoginButton" onClick={LoginButtonClick}>Login</div>
        )
    }

    function LogoutButton() {
        return (
            <div className="NavbarItem" onClick={LogoutButtonClick}>Logout</div>
        )
    }

    return (
        <>
            <nav className="Navbar">
                <NavLink to="/" className="HomeNavbarItem">   
                    <img src={reactLogo} alt="..." className="AppHeaderLogo" />
                </NavLink>
                <ul>
                    <ViewPageButton dest="/user/myusername">Tweets</ViewPageButton>
                    <ViewPageButton dest="/students">Students</ViewPageButton>
                    {!user &&
                        <LoginButton/>
                    }
                    {user &&
                        <LogoutButton/>
                    }
                </ul>
            </nav>
        </>
    )
}