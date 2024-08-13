import { useState, useEffect, useRef } from 'react'

import "../../css/App.css"
import "../../css/Students.css"

import Layout from './Layout.tsx'

import { LoadingFrameFullScreen} from "../../utils/Library.js"
import { useCookies } from 'react-cookie'

export default function AddStudent() {
    const [successMessage, setSuccessMessage] = useState(false)
    const [errorMessage, setErrorMessage] = useState("")
    const [emptyFields, setEmptyFields] = useState("")

    const [loading, setLoading] = useState(false)

    const firstNameRef = useRef<HTMLInputElement>(null)
    const lastNameRef = useRef<HTMLInputElement>(null)
    const emailRef = useRef<HTMLInputElement>(null)

    const [cookies] = useCookies();

    // Submit Button
    async function runSubmit(e : React.MouseEvent) {
        e.preventDefault()

        let firstName = firstNameRef.current?.value
        let lastName = lastNameRef.current?.value
        let email = emailRef.current?.value

        // Check Fields
        let invalidInput = false
        setEmptyFields("")
        setSuccessMessage(false)

        const emailRegex = /\S+@\S+\.\S+/;
        let emailResult = emailRegex.test(email!)
        if (!emailResult) {
            invalidInput = true
            setEmptyFields((prevState) => (prevState + " email"))
        }

        if (firstName == "") {
            invalidInput = true
            setEmptyFields((prevState) => (prevState + " firstName"))
        }

        if (lastName == "") {
            invalidInput = true
            setEmptyFields((prevState) => (prevState + " lastName"))
        }

        // Check Invalid
        if (invalidInput) {
            return setErrorMessage("There are Error(s) in your Input.")
        }

        // Set Loading
        setLoading(true)
        
        fetchAddStudent(firstName!, lastName!, email!)
        .finally(() => {
            setLoading(false)
        })
    }

    // Create Testing Data
    async function runTestingData() {
        for (let i = 0 ; i < 1; i++) {
            let email = (Math.random() * 10000).toString() + "@gmail.com"
            fetchAddStudent("Bob", "Smith", email!)
        }
    }

    // Add Student
    async function fetchAddStudent(first : string, last : string, email: string) {
        try {
            // Fetch
            let body = {
                firstName: first,
                lastName: last,
                email: email,
            }
            const response = await fetch("http://localhost:8080/v1/students/new", {
                method: "POST",
                headers: {
                    "Content-type": "application/json",
                    "X-XSRF-TOKEN": cookies["XSRF-TOKEN"]
                },
                credentials: "include",
                body: JSON.stringify(body)
            })
            const data = await response.json()

            // Set Success Message
            setSuccessMessage(false)
            setErrorMessage("")
            if (response.ok) {
                setSuccessMessage(true)
            } else if (data.message == "email-in-use") {
                setErrorMessage("This Email is already in use.")
            } else {
                setErrorMessage("There was an Error with the Server.")
            }
        } catch {}
    }

    return (
        <Layout>
            <div className="AddStudentPage">
                <form className="AddStudentForm">
                    <label className="StudentFormHeader">Add New User</label>
                    <label className="AddStudentFormLabel">First Name</label>
                    <input
                        className={"Input " + (emptyFields.includes("firstName") ? "InputError" : "")}
                        ref={firstNameRef}
                    />

                    <label className="AddStudentFormLabel">Last Name</label>
                    <input
                        className={"Input " + (emptyFields.includes("lastName") ? "InputError" : "")}
                        ref={lastNameRef}
                    />

                    <label className="AddStudentFormLabel">Email</label>
                    <input
                        className={"Input " + (emptyFields.includes("email") ? "InputError" : "")}
                        ref={emailRef}
                    />

                    <button 
                        className="ButtonRounded ButtonBlue ButtonBold ButtonTextLarge" 
                        onClick={runSubmit}
                        disabled={loading}>
                        Submit
                    </button>

                    <button 
                        className="ButtonRounded ButtonRed ButtonBold ButtonTextLarge" 
                        onClick={runTestingData}
                        disabled={loading}>
                        Create Testing Data
                    </button>

                    {successMessage &&
                        <div className="AddSuccessMessage">
                            Success!
                        </div>
                    }

                    {errorMessage != "" &&
                        <div className="AddErrorMessage">
                            {errorMessage}
                        </div>
                    }

                    {loading &&
                        <LoadingFrameFullScreen loading={loading}/>
                    }
                </form>
            </div>
        </Layout>
    )
}