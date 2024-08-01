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

// Pages
import HomePage from "./pages/Home/Home"
import AboutPage from "./pages/About/About"

// Auth Page
import LoginPage from './pages/Auth/Login.js';
import RegisterPage from './pages/Auth/Register.js';

// Students Page
import StudentDatabasePage from "./pages/StudentDatabase/Database.jsx";
import AddStudentPage from "./pages/StudentDatabase/AddStudent.jsx"
import ViewStudentPage from './pages/StudentDatabase/ViewStudent.js';
import EditStudentPage from './pages/StudentDatabase/EditStudent.js';

import AppLayout from './Layout.js';

const router = createBrowserRouter(
  createRoutesFromElements(
    <Route path="/" element = {<AppLayout/>}>
      {/* Home */}
      <Route index element={<HomePage/>}/>

       {/* Auth */}
      <Route path="/login" element={<LoginPage/>}/>
      <Route path="/register" element={<RegisterPage/>}/>

      {/* Students */}
      <Route path="/students" element={<StudentDatabasePage/>}/>
      <Route path="/students/view" element={<ViewStudentPage/>}/>
      <Route path="/students/add-student" element={<AddStudentPage/>}/>
      <Route path="/students/edit" element={<EditStudentPage/>}/>
      
      <Route path="/about" element={<AboutPage/>}/>
    </Route>
  )
);

export default function App() {
  return (
    <RouterProvider router={router}/>
  )
}
