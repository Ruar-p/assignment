import React, { useState, useEffect } from 'react';
import { Student, CreateStudentRequest, UpdateStudentRequest } from '../../types/student';
import { studentService } from '../../services/studentApi';
import { authService } from '../../services/api';
import StudentTable from './StudentTable';
import AddStudentModal from './AddStudentModal';
import EditStudentModal from './EditStudentModal';
import { useNavigate } from 'react-router-dom';

function StudentPage() {
    // For logout
    const navigate = useNavigate();

    const [students, setStudents] = useState<Student[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null> (null);

    // Modal states
    const [isAddModalOpen, setIsAddModalOpen] = useState(false);
    const [isEditModalOpen, setIsEditModalOpen] = useState(false);
    const [currentStudent, setCurrentStudent] = useState<Student | null>(null);

    // Load students on component mount
    useEffect(() => {
        fetchStudents();
    }, []);

    const fetchStudents = async () => {
        try {
            setLoading(true);
            const data = await studentService.getAll();
            setStudents(data);
            setError(null);
        } catch (err) {
            setError("Failed to fetch students");
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    // Add student
    const handleAddStudent = async (studentData: CreateStudentRequest) => {
        try {
            await studentService.createNewStudent(studentData);
            fetchStudents(); // Refresh list
            setIsAddModalOpen(false);
        } catch (err) {
            setError("Failed to add student");
            console.error(err);
        }
    };

    // Open edit modal for chosen student
    const handleEditStudent = (student: Student) => {
        setCurrentStudent(student);
        setIsEditModalOpen(true);
    };

    // Update student
    const handleUpdateStudent = async (id: string, studentData: UpdateStudentRequest) => {
        try {
            await studentService.update(id, studentData);
            fetchStudents();
            setIsEditModalOpen(false);
        } catch (err) {
            setError("failed to update student");
            console.error(err);
        }
    };

    // Delete student
    const handleDeleteStudent = async (id: string) => {
        if (window.confirm("Are you sure you want to delete this student?")) {
            try {
                await studentService.delete(id);
                fetchStudents();    // Refresh list
            } catch (err) {
                setError("failed to delete student");
            }
        }
    };

    // Log out handler
    const handleLogout = async () => {
        try {
            await authService.logout();
            navigate("/login"); // Redirect to login page
        } catch (error) {
            console.error("Logout failed", error);
        }
    };

    return (
        <div className="student-page">
            <div className="header">
                <h1>Student Management</h1>
                <button
                    onClick={handleLogout}
                    className="logout-button"
                >
                    Logout
                </button>
            </div>
            

            {error && <div className="error-message">{error}</div>}

            <div className="actions">
                <button onClick={() => setIsAddModalOpen(true)}>Add New Student</button>
            </div>

            {loading ? (
                <p>Loading students...</p>
            ) : (
                <StudentTable
                    students={students}
                    onEdit={handleEditStudent}
                    onDelete={handleDeleteStudent}
                />
            )}

            <AddStudentModal
                isOpen={isAddModalOpen}
                onClose={() => setIsAddModalOpen(false)}
                onSubmit={(handleAddStudent)}
            />

            <EditStudentModal
                student={currentStudent}
                isOpen={isEditModalOpen}
                onClose={() => setIsEditModalOpen(false)}
                onSubmit={handleUpdateStudent}
            />
        </div>
    );
}


export default StudentPage;