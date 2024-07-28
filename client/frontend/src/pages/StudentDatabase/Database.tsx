import { useState, useEffect, useRef } from 'react'
import {useNavigate} from "react-router-dom";
import { useSearchParams } from 'react-router-dom';

import "../../css/App.css"
import "../../css/Students.css"

import ViewIcon from "../../images/ViewIcon.png"
import EditIcon from "../../images/EditIcon.png"
import DeleteIconWhite from "../../images/DeleteIconWhite.png"
import SearchIcon from "../../images/SearchIcon.png"

import Layout from './Layout.js';

import { LoadingFrameFullScreen } from "../../library/Library.js"

// Schemas
import { Student, StudentSchema } from './Schema/StudentSchema.tsx';

export default function Database() {
    const [students, setStudents] = useState<Student[]>([])
    const [loading, setLoading] = useState(false)
    const navigate = useNavigate()
    const searchRef = useRef<HTMLInputElement>(null)
    // Search & Pagination
    const [searchParams, setSearchParams] = useSearchParams()
    const [pageResult, setPageResult] = useState({
        pageNumber: 0,
        pageSize: 0,
        totalPages: 0
    })
    const search = searchParams.get("search") || ""
    const pageNumber = Number(searchParams.get("pageNumber") || 0)
    function getSearchParams() {
        return {
            "search": String(search),
            "pageNumber": String(pageNumber)
        }
    }

    // Load Students at Start
    // Update Students on Params Update
    useEffect(() => {
       fetchAllStudents()
    }, [searchParams])

    // Get All Students
    async function fetchAllStudents() {
        setLoading(true)

        try {
            // Fetch
            const params = new URLSearchParams(getSearchParams())
            const response = await fetch(`http://localhost:8080/students?${params}`, {
                method: "GET"
            })
            const data = await response.json()

            if (!response.ok) {throw new Error()}

            // Get Content & Set Pagination Info
            const list = data.content
            setPageResult(data)
            
            // Check Type Schema's with Zod
            let validListOfSchema = true
            list.forEach((v : Student) => {
                if (!validListOfSchema) {return}
                const validSchema = StudentSchema.safeParse(v).success
                if (!validSchema) {
                    validListOfSchema = false
                }
            })
            if (!validListOfSchema) {throw new Error("Data incorrect input format")}
            
            // Set Data
            setStudents(list)
        } catch {}

        setLoading(false)
    }

    // Delete Student
    async function deleteStudent(data : Student) {
        setLoading(true)
        
        let ID = data.id
        try {
            const response = await fetch(`http://localhost:8080/students/delete/${ID}`, {
                method: "DELETE",
            })
            if (response.status == 200) {
                // Refresh Students
                await fetchAllStudents()
            }
        } catch {}

        setLoading(false)
    }

    // View Student
    function viewStudent(id : number) {
        navigate(`/students/view?id=${id}`)
    }

    // Edit Student
    function editStudent(id : number) {
        navigate(`/students/edit?id=${id}`)
    }

    function StudentRowHeader() {
        return (
            <div className="StudentFrameHeader">
                <div className="StudentFrameBox">ID</div>
                <div className="StudentFrameBox">First Name</div>
                <div className="StudentFrameBox">Last Name</div>
                <div className="StudentFrameBox">Email</div>
                <div className="StudentFrameBox">Actions</div>
            </div>
        )
    }

    function StudentRowDisplay({data, rowIndex} : {data : Student, rowIndex : number}) {
        let rowModifier = ""
        if (rowIndex % 2 == 0) {
            rowModifier = "StudentFrameEvenRowColor"
        }
        return (
            <div className={"StudentFrame " + rowModifier}>
                <div className="StudentFrameBox">{data.id}</div>
                <div className="StudentFrameBox">{data.firstName}</div>
                <div className="StudentFrameBox">{data.lastName}</div>
                <div className="StudentFrameBox">{data.email}</div>
                <div className="StudentFrameBox StudentFrameActionButtons">
                    <img 
                        src={ViewIcon} 
                        className="StudentFrameActionButton ViewIcon"
                        onClick={() => viewStudent(data.id)}
                    />
                    <img 
                        src={EditIcon} 
                        className="StudentFrameActionButton EditIcon"
                        onClick={() => editStudent(data.id)}
                    />
                    <img 
                        src={DeleteIconWhite} 
                        className="StudentFrameActionButton DeleteIcon"
                        onClick={() => deleteStudent(data)}
                    />
                </div>
            </div>
        )
    }

    // Navigation Buttons & Pagination
    function PaginationInfo() {
        let pageNum = pageResult.totalPages > 0 ? pageResult.pageNumber + 1 : 0
        return (
            <div>
                {pageNum} / {pageResult.totalPages}
            </div>
        )
    }

    function ForwardButtonClick() {
        // Check
        if (pageResult.pageNumber >= pageResult.totalPages - 1) {return}
        if (loading) {return}
        // Set Loading
        setLoading(true)
        // Set New Page Number
        if (pageNumber < pageResult.totalPages) {
            // Get Params & Push New Params
            let newSearchParams = getSearchParams()
            newSearchParams["pageNumber"] = String(pageNumber + 1)
            setSearchParams(newSearchParams)
        }
    }

    function ForwardButton() {
        return (
            <button
                className="NavigateButtonsForward"
                onClick={ForwardButtonClick}
                disabled={loading}
            >
                {">"}
            </button>
        )
    }

    function BackButtonClick() {
        // Check
        if (pageResult.pageNumber <= 0) {return}
        if (loading) {return}
        // Set Loading
        setLoading(true)
        // Set New Page Number
        if (pageNumber < pageResult.totalPages) {
            // Get Params & Push New Params
            let newSearchParams = getSearchParams()
            newSearchParams["pageNumber"] = String(pageNumber - 1)
            setSearchParams(newSearchParams)
        }
    }

    function BackButton() {
        return (
            <button
                className="NavigateButtonsBackward"
                onClick={BackButtonClick}
                disabled={loading}
            >
                {"<"}
            </button>
        )
    }

    // Search Bar
    function SearchBar() {    
        // Set Search Params in URL - Get Params & Push New Params
        function runSubmitForm(e : React.FormEvent) {
            e.preventDefault()

            const searchText = searchRef.current?.value || ""
            let newSearchParams = getSearchParams()
            newSearchParams["search"] = searchText
            newSearchParams["pageNumber"] = String(0)
            setSearchParams(newSearchParams)
        }

        return (
            <form 
                className="SearchBarForm"
                onSubmit={runSubmitForm}
            >
                <input
                    className="StudentSearchBar"
                    type="search"
                    role="searchbox"
                    placeholder="Search..."
                    ref={searchRef}
                    defaultValue={search}
                >
                </input>
                <img
                    className="SearchSubmitButton" 
                    src={SearchIcon}
                    onClick={runSubmitForm}
                >
                </img>
            </form>
        )
    }

    return (
        <Layout>
            <div className="StudentsPage">
                {/* Search Bar */}
                <SearchBar/>
                {/* Content */}
                <div>
                    <StudentRowHeader/>
                    {students
                    .map((student, index) => {
                        return <StudentRowDisplay
                            key={student.id} 
                            data={student}
                            rowIndex={index}/>
                    })}
                </div>
                <div className="NavigateButtonsRow">
                    <BackButton/>
                    <ForwardButton/>
                </div>
                <div className="NavigatePaginationInfo">
                    <PaginationInfo/>
                </div>
                {/* Loading */}
                {loading &&
                    <LoadingFrameFullScreen loading={loading}/>
                }
            </div>
        </Layout>
    )
}