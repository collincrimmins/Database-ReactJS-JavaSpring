import React, {useContext, useState, useEffect} from 'react'
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

    const navigate = useNavigate()
    
    useEffect(() => {
       console.log(user)
    }, [user])

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
        user, 
        updateUser,
        logoutUser,
        userAuthToken
    }

    return (
        <AuthContext.Provider value={contextValues}>
            {children}
        </AuthContext.Provider>
    )
}