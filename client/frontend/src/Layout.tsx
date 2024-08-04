import React, { Suspense } from 'react';
import { Outlet } from 'react-router-dom';
import Navbar from "./Navbar";
import { AuthProvider } from './contexts/useAuthContext';
import { CookiesProvider } from 'react-cookie';

export default function AppLayout() {
    return (
        <Suspense fallback={null}>
            <CookiesProvider>
                <AuthProvider>
                    <Navbar/>
                    <Outlet/>
                </AuthProvider>
            </CookiesProvider>
        </Suspense>
    )
}