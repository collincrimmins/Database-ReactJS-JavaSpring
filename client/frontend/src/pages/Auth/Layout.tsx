import React, { useState, useEffect, useRef } from 'react'
import { useNavigate, useLocation } from "react-router-dom";

import "../../css/App.css"
import "../../css/Auth.css"

export default function Layout({children} : {children : React.ReactNode}) {
    const navigate = useNavigate()
    
    return (
        <main className = "BodyAuthPage">
            {/* Children */}
            {children}
        </main>
    )
}