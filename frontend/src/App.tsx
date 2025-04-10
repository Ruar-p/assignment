import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Login from './components/Login';
import Register from './components/Register';
import StudentPage from './components/students/StudentPage';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/students" element={<StudentPage />} />
        <Route path="/home" element={<Navigate to="/students" />} />
        <Route path="/" element={<Navigate to="/login" />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
