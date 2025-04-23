import React from 'react';
import { Student } from '../../types/student';


interface StudentTableProps {
    students: Student[];
    onEdit: (student: Student) => void;
    onDelete: (id: string) => void;
}

const StudentTable = ({ students, onEdit, onDelete }: StudentTableProps) => {

    // Update date formatting to not include timezone
    const formatDate = (dateString: string) => {
        try {
            // Create a date object directly from the full ISO string
            const date = new Date(dateString);
            
            // Format using year, month, day directly from the date
            // Use UTC methods to get the date components
            // 
            const year = date.getUTCFullYear();
            const month = date.getUTCMonth() + 1; // Month is 0-indexed
            const day = date.getUTCDate();
            
            // Return formatted date (e.g., "4/23/2025")
            return `${month}/${day}/${year}`;
            
        } catch (e) {
            return dateString;
        }
    }

    return (
        <table className="student-table">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Courses</th>
                    <th>Phone Number</th>
                    <th>Date of Birth</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                {students.map(student => (
                    <tr key={student.id}>
                        <td>{student.name}</td>
                        <td>{student.courses.join(', ')}</td>
                        <td>{student.phoneNumber}</td>
                        <td>{formatDate(student.dateOfBirth)}</td>
                        <td>
                            <button onClick={() => onEdit(student)}>Edit</button>
                            <button onClick={() => onDelete(student.id!)}>Delete</button>
                        </td>
                    </tr>
                ))}
            </tbody>
        </table>
    );
};

export default StudentTable;