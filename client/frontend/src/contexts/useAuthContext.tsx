import React, {useContext, useState, useEffect} from 'react'
import { useCookies } from 'react-cookie'
import { useNavigate } from 'react-router-dom'

const AuthContext = React.createContext<any>({})

export function useAuthContext() {
    return useContext(AuthContext)
}

interface UserType {
    token: string,
    id: string | null,
    username: string | null,
    photo: string | null,
}

type AuthProviderProps = {
    children: React.ReactNode
}
export function AuthProvider({children} : AuthProviderProps) {
    const [user, setUser] = useState<UserType | null>(null)
    const [userAuthToken, setUserAuthToken] = useState<string | null>(null)

    const navigate = useNavigate()
    const [cookies] = useCookies();
    
    useEffect(() => {
       //console.log(user)
    }, [user])

    // Get Token from LocalStorage
    useEffect(() => {
        if (!user) {
            let AuthToken = localStorage.getItem("AuthToken")
            if (AuthToken) {
                setUserAuthToken("Bearer " + AuthToken)
                setUser({
                    token: AuthToken,
                    id: null,
                    username: null,
                    photo: null
                })
            }
        }
    }, [])

    // User
    useEffect(() => {
        if (user) {
            // Check Token
            const token = user.token
            fetchCheckToken(token)
            // Get my UserProfile
            fetchUserProfile(token)
        }
    }, [user])

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

    // Get my UserProfile
    async function fetchUserProfile(token : string) {
        // UserProfile already Exists
        if (user) {
            if (user.id != null) {
                return
            }
        }

        try {
            // Fetch
            const body = {
                token: token
            }
            const response = await fetch(`http://localhost:8080/users/getmyprofile`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(body)
            })
            const data = await response.json()
            if (!response.ok) {throw new Error()}

            if (user) {
                user.id = data.id
                user.username = data.username
                user.photo = data.photo
                setUser({...user})
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
            token: AuthToken,
            id: null,
            username: null,
            photo: null
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
    }

    return (
        <AuthContext.Provider value={contextValues}>
            {children}
        </AuthContext.Provider>
    )
}