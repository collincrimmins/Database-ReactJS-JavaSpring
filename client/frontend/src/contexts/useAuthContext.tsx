import React, {useContext, useState, useEffect} from 'react'
import { useCookies } from 'react-cookie'
import { useNavigate } from 'react-router-dom'

const AuthContext = React.createContext<any>({})

export function useAuthContext() {
    return useContext(AuthContext)
}

interface UserType {
    username: string,
    token : string,
}

type AuthProviderProps = {
    children: React.ReactNode
}
export function AuthProvider({children} : AuthProviderProps) {
    const [user, setUser] = useState<UserType | null>(null)
    const [userAuthToken, setUserAuthToken] = useState<string | null>(null)
    //const [csfrToken, setCsfrToken] = useState<string | null>(null)

    const navigate = useNavigate()
    const [cookies] = useCookies();
    
    useEffect(() => {
       //console.log(user)
    }, [user])

    // useEffect(() => {
    //     //console.log(csfrToken)
    // }, [csfrToken])

    // Get Token from LocalStorage
    useEffect(() => {
        if (!user) {
            let AuthToken = localStorage.getItem("AuthToken")
            if (AuthToken) {
                setUserAuthToken("Bearer " + AuthToken)
                setUser({
                    username: "username",
                    token: AuthToken
                })
            }
        }
    }, [])

    // Get CSFR Token for this Session
    // useEffect(() => {
    //     fetchCSFRToken()
    // }, [])

    // Updates on User
    useEffect(() => {
        // Check if User Token is Valid
        if (user) {
            const token = user.token
            fetchCheckToken(token)
        }
        // Refresh CSFR Token after Login/Logout
        //fetchCSFRToken()
    }, [user])

    // Get CSFR Token for this Session
    // async function fetchCSFRToken() {
    //     try {
    //         // Fetch
    //         const response = await fetch(`http://localhost:8080/csrf`, {
    //             method: "GET",
    //             headers: {
    //                 "Content-Type": "application/json",
    //             }
    //         })
    //         const data = await response.json()
            
    //         if (!response.ok) {throw new Error()}
            
    //         // Set CSFR Token
    //         setCsfrToken(data.token)
    //     } catch {}
    // }

    // Check if Token is Valid
    async function fetchCheckToken(token : string) {
        try {
            // Fetch
            const body = {
                token: token
            }
            const response = await fetch(`http://localhost:8080/auth/checkToken`, {
                method: "POST",
                headers: {
                    "Content-type": "application/json",
                    "X-XSRF-TOKEN": cookies["XSRF-TOKEN"]
                },
                credentials: "include",
                body: JSON.stringify(body)
            })
            const data = await response.json()
            
            if (!response.ok) {throw new Error()}
            
            if (data.message == "invalid-token") {
                // Force Logout
                logoutUser()
            }
        } catch {}
    }

    // Sign In
    function updateUser(AuthToken : string) {
        // Set LocalStorage
        localStorage.setItem("AuthToken", AuthToken)
        // Set User
        setUserAuthToken("Bearer " + AuthToken)
        setUser({
            username: "username",
            token: AuthToken
        })
    }

    // Logout
    function logoutUser() {
        // Set LocalStorage
        localStorage.removeItem("AuthToken")
        // Set User
        setUserAuthToken(null)
        setUser(null)
        // Navigate
        navigate("/")
    }

    const contextValues = {
        // user
        user, 
        updateUser,
        logoutUser,
        userAuthToken,

        // csfr
        //csfrToken
    }

    return (
        <AuthContext.Provider value={contextValues}>
            {children}
        </AuthContext.Provider>
    )
}