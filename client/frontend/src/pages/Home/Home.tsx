import { useState, useEffect, useRef } from 'react'

import {
    createBrowserRouter,
    Routes,
    Route,
    Link,
    NavLink,
    useNavigate,
    RouterProvider,
    createRoutesFromElements,
  } from "react-router-dom";

import "../../css/App.css"

export default function Home() {
    const navigate = useNavigate()

    // Go-to Page on Startup
    useEffect(() => {
        navigate("/students", { replace: true })
    }, [])

    return (
        <div className = "PageHeader">
            Home Page
        </div>
    )
}