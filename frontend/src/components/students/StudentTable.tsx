import React from 'react';
import { Student } from '../../types/student';


interface StudentTableProps {
    students: Student[];
    onEdit: (student: Student) => void;
    onDelete: (id: string) => void;
}

const StudentTable = ({ students, onEdit, onDelete }: StudentTableProps) => {
    // Format date for display
    const formatDate = (dateString: string) => {
        try {
          const date = new Date(dateString);
          return date.toLocaleDateString();
        } catch (e) {
          return dateString;
        }
    };

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