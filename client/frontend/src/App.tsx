import { useState, useEffect, useRef } from 'react'
import reactLogo from './images/react.svg'
import './css/App.css'

import {
  createBrowserRouter,
  Routes,
  Route,
  Link,
  NavLink,
  useNavigate,
  RouterProvider,
  createRoutesFromElements,
} from "react-router-dom";

// Layout
import RootLayout from './layouts/RootLayout';

// Pages
import HomePage from "./pages/Home/Home"
import AboutPage from "./pages/About/About"

// Students Page
import StudentDatabasePage from "./pages/StudentDatabase/Database.jsx";
import AddStudentPage from "./pages/StudentDatabase/AddStudent.jsx"
import ViewStudentPage from './pages/StudentDatabase/ViewStudent.js';
import EditStudentPage from './pages/StudentDatabase/EditStudent.js';

const router = createBrowserRouter(
  createRoutesFromElements(
    <Route path="/" element = {<RootLayout/>}>
       <Route index element={<HomePage/>}/>

       <Route path="students" element={<StudentDatabasePage/>}/>
       <Route path="students/view" element={<ViewStudentPage/>}/>
       <Route path="students/add-student" element={<AddStudentPage/>}/>
       <Route path="students/edit" element={<EditStudentPage/>}/>
       
       <Route path="about" element={<AboutPage/>}/>
    </Route>
  )
);

export default function App() {
  return (
      <RouterProvider router={router}/>
  )
}
