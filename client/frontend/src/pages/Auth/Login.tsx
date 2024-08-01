import { useState, useEffect, useRef } from 'react'
import { useNavigate, useLocation } from "react-router-dom";

import "../../css/App.css"
import "../../css/Auth.css"

import Layout from './Layout.tsx'

import { LoadingFrameFullScreen} from "../../utils/Library.js"
import { useAuthContext } from '../../contexts/useAuthContext.tsx';

export default function Login() {
    const [successMessage, setSuccessMessage] = useState("")
    const [errorMessage, setErrorMessage] = useState("")
    const [emptyFields, setEmptyFields] = useState("")

    const [loading, setLoading] = useState(false)

    const emailRef = useRef<HTMLInputElement>(null)
    const passwordRef = useRef<HTMLInputElement>(null)

    const {user, updateUser} = useAuthContext()

    const navigate = useNavigate()

    // Redirect if User Valid
    useEffect(() => {
        if (user != null) {
            navigate("/")
            return
        }
    }, [user])

    // Submit Button
    async function runSubmit(e : React.MouseEvent) {
        e.preventDefault()

        let email = emailRef.current?.value
        let password = passwordRef.current?.value

        // Check Fields
        let invalidInput = false
        setEmptyFields("")
        setSuccessMessage("")

        const emailRegex = /\S+@\S+\.\S+/;
        let emailResult = emailRegex.test(email!)
        if (!emailResult) {
            invalidInput = true
            setEmptyFields((prevState) => (prevState + " email"))
        }

        if (password == "") {
            invalidInput = true
            setEmptyFields((prevState) => (prevState + " password"))
        }


        // Check Invalid
        if (invalidInput) {
            return setErrorMessage("There are Error(s) in your Input.")
        }

        // Set Loading
        setLoading(true)
        
        fetchLogin(email!, password!)
        .finally(() => {
            setLoading(false)
        })
    }

    function successLogin(data : any) {
        // Set Token
        updateUser(data.token)

        // Navigate
        navigate("/")
    }

    // Login
    async function fetchLogin(email : string, password : string) {
        try {
            // Fetch
            let body = {
                email: email,
                password: password,
            }
            const response = await fetch("http://localhost:8080/auth/login", {
                method: "POST",
                headers: {
                    "Content-type": "application/json",
                },
                body: JSON.stringify(body)
            })
            const data = await response.json()

            // Set Messages
            setSuccessMessage("")
            setErrorMessage("")
            if (response.ok) {
                setSuccessMessage("Success!")
                successLogin(data)
            } else if (data.message == "auth-failed") {
                setErrorMessage("Incorrect email & password combination.")
            } else {
                setErrorMessage("There was an Error with the Server.")
            }
        } catch {}
    }

    function goToRegister(e : React.MouseEvent) {
        e.preventDefault()
        
        navigate("/register")
    }

    return (
        <Layout>
            <form className="BodyForm">
                <div className="BodyFormTop">
                    <label className="FormHeader">Login</label>
                    <label className="FormLabel">Email</label>
                    <input
                        className={"Input " + (emptyFields.includes("email") ? "InputError" : "")}
                        defaultValue={"123@123.com"}
                        ref={emailRef}
                    />

                    <label className="FormLabel">Password</label>
                    <input
                        className={"Input " + (emptyFields.includes("password") ? "InputError" : "")}
                        defaultValue={"123"}
                        type="password"
                        ref={passwordRef}
                    />
                </div>
                <div className="BodyFormBottom">
                    <button 
                        className="ButtonRounded ButtonBlue ButtonBold ButtonTextLarge" 
                        onClick={runSubmit}
                        disabled={loading}>
                        Submit
                    </button>

                    <button 
                        className="RegisterText" 
                        onClick={goToRegister}
                        disabled={loading}>
                        Register an Account?
                    </button>

                    {successMessage != "" &&
                        <div className="FormSuccessMessage">
                            {successMessage}
                        </div>
                    }

                    {errorMessage != "" &&
                        <div className="FormErrorMessage">
                            {errorMessage}
                        </div>
                    }

                    {loading &&
                        <LoadingFrameFullScreen loading={loading}/>
                    }
                </div>
            </form>
        </Layout>
    )
}