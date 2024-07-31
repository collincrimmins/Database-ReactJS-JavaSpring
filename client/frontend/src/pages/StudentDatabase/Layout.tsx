import React, { useState, useEffect, useRef } from 'react'
import { useNavigate, useLocation } from "react-router-dom";

import "../../css/App.css"
import "../../css/Students.css"

import DatabaseIcon from "../../images/DatabaseIcon.png"
import AddIcon from "../../images/AddIcon.png"
import EmptyIcon from "../../images/EmptyIcon.png"

export default function Layout({children} : {children : React.ReactNode}) {
    const navigate = useNavigate()
    const { pathname, search, hash } = useLocation() // /products | ?id=15 | #pricing

    // Set Page on Navbar Button Click
    type ThisPageProps = {
        thispage : string
    }
    function NavbarButtonClicked({thispage} : ThisPageProps) {
        navigate(thispage)
    }

    // Header Navbar Buttons
    function NavbarButton({thispage} : ThisPageProps) {
        let image = EmptyIcon
        let pageInView = false
        if (thispage == "/students") {
            image = DatabaseIcon
            if (pathname == "/students" || pathname == "/students/view" || pathname == "/students/edit") {
                pageInView = true
            }
        } else if (thispage == "/students/add-student") {
            image = AddIcon
            if (pathname == "/students/add-student") {
                pageInView = true
            }
        }
        
        return (
            <div 
                className={`PageNavbarButton ${pageInView ? "PageNavbarButtonActive" : ""}`}
                onClick={() => NavbarButtonClicked({thispage})}
            >
                <img className = "PageNavbarButtonImage" src={image}/>
            </div>
        )
    }

    return (
        <main className = "Body">
            {/* Header */}
            <div className = "PageHeader">
                Student Database
            </div>
            {/* Select Page Navbar */}
            <div className="PageNavbar">
                <NavbarButton thispage="/students"/>
                <NavbarButton thispage="/students/add-student"/>
            </div>
            {/* Children */}
            {children}
        </main>
    )
}