import { useState, useEffect, useRef } from 'react'
import { Dispatch, SetStateAction } from "react";
import {useNavigate} from "react-router-dom";

import "../../css/App.css"
import "../../css/Students.css"

import { LoadingFrameFullScreen } from "../../utils/Library.js"
import { useSearchParams } from 'react-router-dom';

import Layout from './Layout.tsx'

// Schemas
import { Student, StudentSchema } from './Schema/StudentSchema.tsx';

export default function ViewStudent() {
    const [data, setData] = useState<Student | null>(null)
    const [loading, setLoading] = useState(false)
    const [searchParams, setSearchParams] = useSearchParams()
    const navigate = useNavigate()

    const idQuery = searchParams.get("id") || ""

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
            const data = await response.json()
            
            if (!response.ok) {throw new Error()}

            // Check Data with Zod
            const validSchema = StudentSchema.safeParse(data)
            if (!validSchema.success) {throw new Error()}
            
            // Set Data
            setData(data)
        } catch (error) {}
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
            <div>
                {data &&
                    <div className="ViewStudentsPage">
                        <div className="CloseButtonRow">
                            <CloseButton/>
                        </div>
                        <div className="ViewStudentRow">
                            <div className="ViewStudentTitle">
                                Name:
                            </div>
                            {data.firstName} {data.lastName}
                        </div>
                        <div className="ViewStudentLineDivider"/>
                        <div className="ViewStudentRow">
                            <div className="ViewStudentTitle">
                                Email:
                            </div>
                            {data.email}
                        </div>
                        <div className="ViewStudentLineDivider"/>
                        <div className="ViewStudentRow">
                            <div className="ViewStudentTitle">
                                ID:
                            </div>
                            {data.id}
                        </div>
                    </div>
                }
                {!data && 
                    <div>
                        Student does not exist.
                    </div>} 
                {loading &&
                    <LoadingFrameFullScreen loading={loading}/>
                }
            </div>
        </Layout>
    )
}