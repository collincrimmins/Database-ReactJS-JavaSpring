import React, { Suspense } from 'react';
import { Outlet } from 'react-router-dom';
import Navbar from "./Navbar";
import { AuthProvider } from './contexts/useAuthContext';

export default function AppLayout() {
    return (
        <Suspense fallback={null}>
            <AuthProvider>
                <Navbar/>
                <Outlet/>
            </AuthProvider>
        </Suspense>
    )
}