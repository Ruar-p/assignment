import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Login from './components/Login';
import Register from './components/Register';
import StudentPage from './components/students/StudentPage';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import ChatPage from './components/chat/ChatPage';

function App() {
  return (
    <AuthProvider>  {/* Global authentication context */}
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />

          {/* Protected routes */}
          <Route element={<ProtectedRoute />}>
            <Route path="/students" element={<StudentPage />} />
            <Route path="/chat" element={<ChatPage />} />
            {/* Future protected routes go here */}
          </Route>
          
          <Route path="/home" element={<Navigate to="/students" />} />
          <Route path="/" element={<Navigate to="/login" />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
};

export default App;
