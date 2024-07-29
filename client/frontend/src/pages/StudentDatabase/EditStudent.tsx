import { useState, useEffect, useRef } from 'react'
import { useSearchParams } from 'react-router-dom';
import {useNavigate} from "react-router-dom";

import "../../css/App.css"
import "../../css/Students.css"

import Layout from './Layout.js'

import { LoadingFrameFullScreen} from "../../library/Library.js"

// Schemas
import { Student, StudentSchema } from './Schema/StudentSchema.tsx';

export default function EditStudent() {
    const [data, setData] = useState<Student | null>(null)
    const [successMessage, setSuccessMessage] = useState("")
    const [errorMessage, setErrorMessage] = useState("")
    const [emptyFields, setEmptyFields] = useState("")
    const [loading, setLoading] = useState(false)
    const [searchParams, setSearchParams] = useSearchParams()
    const navigate = useNavigate()

    const idQuery = searchParams.get("id") || ""

    const firstNameRef = useRef<HTMLInputElement>(null)
    const lastNameRef = useRef<HTMLInputElement>(null)
    const emailRef = useRef<HTMLInputElement>(null)

    // Startup get student info
    useEffect(() => {
        startupFetchStudent()
    }, [])

    async function startupFetchStudent() {
        setLoading(true)
        await fetchStudent()
        setLoading(false)
    }

    // Get Student by ID
    async function fetchStudent() {
        try {
            // Fetch
            const response = await fetch(`http://localhost:8080/students/student/${idQuery}`, {
                method: "GET",
            })

            if (!response.ok) {throw new Error()}

            // Check Data with Zod
            const data = await response.json()
            const validSchema = StudentSchema.safeParse(data)
            if (!validSchema.success) {throw new Error()}
            
            // Set Data
            setData(data)
        } catch (error) {}
    }

    async function runSubmit(e : React.MouseEvent) {
        e.preventDefault()

        let firstName = firstNameRef.current?.value
        let lastName = lastNameRef.current?.value
        let email = emailRef.current?.value

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
        
        submitEditStudent(firstName!, lastName!, email!)
        .finally(() => {
            setLoading(false)
        })
    }

    // Add Student
    async function submitEditStudent(first : string, last : string, email: string) {
        try {
            // Fetch
            let body = {
                firstName: first,
                lastName: last,
                email: email,
            }
            const response = await fetch(`http://localhost:8080/students/update/${idQuery}`, {
                method: "PUT",
                headers: {
                    "Content-type": "application/json",
                },
                body: JSON.stringify(body)
            })
            const data = await response.json()
            
            // Set Success/Error Message
            setSuccessMessage("")
            setErrorMessage("")
            if (response.status == 200) {
                setSuccessMessage("Successfully updated student.")
            } else if (data.message == "email-in-use") {
                setErrorMessage("This Email is already in use.")
            } else {
                setErrorMessage("There was an Error with the Server.")
            }
        } catch (error) {
            console.log(error)
        }
    }

    // Close Button
    function Close(e : React.MouseEvent) {
        e.preventDefault()
        navigate("/students")
    }
    
    function CloseButton() {
        return (
            <button
                onClick={Close}
                className="ViewStudentCloseButton"
            >
                X
            </button>
        )
    }

    return (
        <Layout>
            <div className="CloseButtonRow">
                <CloseButton/>
            </div>
            <div className="AddStudentPage">
                <form className="AddStudentForm">
                    <label className="StudentFormHeader">Edit User</label>
                    <label className="AddStudentFormLabel">User ID: #{idQuery}</label>
                    <label className="AddStudentFormLabel">First Name</label>
                    <input
                        className={"Input " + (emptyFields.includes("firstName") ? "InputError" : "")}
                        ref={firstNameRef}
                        defaultValue={data?.firstName}
                    />

                    <label className="AddStudentFormLabel">Last Name</label>
                    <input
                        className={"Input " + (emptyFields.includes("lastName") ? "InputError" : "")}
                        ref={lastNameRef}
                        defaultValue={data?.lastName}
                    />

                    <label className="AddStudentFormLabel">Email</label>
                    <input
                        className={"Input " + (emptyFields.includes("email") ? "InputError" : "")}
                        ref={emailRef}
                        defaultValue={data?.email}
                    />

                    <button 
                        className="ButtonRounded ButtonGray ButtonBold ButtonTextLarge" 
                        onClick={runSubmit}
                        disabled={loading}>
                        Submit
                    </button>

                    {successMessage != "" &&
                        <div className="AddSuccessMessage">
                            {successMessage}
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