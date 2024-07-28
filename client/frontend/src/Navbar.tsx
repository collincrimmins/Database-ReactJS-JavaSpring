import React, {useState, useEffect} from 'react'
import { Route, NavLink, Link, Routes, useLocation } from 'react-router-dom';

import "./css/App.css"
import "./css/Navbar.css"

import reactLogo from './images/react.svg'

export default function Navbar() {
    //const location = useLocation();
   // const { hash, pathname, search } = location;

    // Button that will be Underlined when Page Active
    type ViewPageButtonProps = {
        dest : string,
        children: React.ReactNode
    }
    function ViewPageButton({dest, children} : ViewPageButtonProps) {
        // Check Page Active
        let pageActive = false
        // if (pathname.includes(dest)) { // if (dest === pathname) {
        //     pageActive = true
        // }
        // Mark Link as Active or not Active
        return (
            <NavLink to={dest} className={`NavbarItem ${pageActive ? "NavbarItemActive" : ""}`}> {children} </NavLink>
            // <Link href={dest} className={`NavbarItem ${pageActive ? "NavbarItemActive" : ""}`}> {children} </Link>
        )
    }

    return (
        <>
            <nav className="Navbar">
                <NavLink to="/" className="HomeNavbarItem">   
                    <img src={reactLogo} alt="..." className="AppHeaderLogo" />
                </NavLink>
                <ul>
                    <ViewPageButton dest="/students">Students</ViewPageButton>
                    <ViewPageButton dest="/about">About</ViewPageButton>
                </ul>
            </nav>
        </>
    )
}