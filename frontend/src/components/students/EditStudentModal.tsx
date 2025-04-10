import React, { useState, useEffect } from 'react';
import { Student, UpdateStudentRequest, COURSE_OPTIONS } from '../../types/student';

interface EditStudentModalProps {
    student: Student | null;
    isOpen: boolean;
    onClose: () => void;
    onSubmit: (id: string, student: UpdateStudentRequest) => void;
}

function EditStudentModal({student, isOpen, onClose, onSubmit }: EditStudentModalProps) {
    const [formData, setFormData] = useState<UpdateStudentRequest>({
        name: '',
        courses: [],
        phoneNumber: '',
        dateOfBirth: ''
    });

    // Update form when student changes
    useEffect(() => {
        if (student) {
            setFormData({
                name: student.name,
                courses: student.courses,
                phoneNumber: student.phoneNumber,
                dateOfBirth: student.dateOfBirth
            });
        }
    }, [student]);

    if (!isOpen || !student) return null;

    // Define what happens when form changes, courses change, or the form is submitted
    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleCoursesChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const selectedOptions = Array.from(e.target.selectedOptions, option => option.value);
        setFormData({ ...formData, courses: selectedOptions });
    }

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        onSubmit(student.id!, formData);
        isOpen = false;
    };

    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <h2>Edit Student</h2>
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="edit-name">Name:</label>
                        <input
                            type="text"
                            id="edit-name"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="edit-courses">Courses:</label>
                        <select
                            id="edit-courses"
                            name="courses"
                            multiple
                            value={formData.courses}
                            onChange={handleCoursesChange}
                            required
                        >
                            {COURSE_OPTIONS.map(course => (
                                <option key={course} value={course}>
                                    {course}
                                </option>
                            ))}
                        </select>
                        <small>Hold Ctrl/Cmd to select multiple courses</small>
                    </div>

                    <div className="form-group">
                        <label htmlFor="edit-phoneNumber">Phone Number:</label>
                        <input
                            type="tel"
                            id="edit-phoneNumber"
                            name="phoneNumber"
                            pattern="[0-9]{10}"
                            placeholder="10 digits, no spaces or dashes"
                            value={formData.phoneNumber}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="edit-dateOfBirth">Date of Birth:</label>
                        <input
                            type="date"
                            id="edit-dateOfBirth"
                            name="dateOfBirth"
                            value={formData.dateOfBirth}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="modal-actions">
                        <button type="submit">Update Student</button>
                        <button type="button" onClick={onClose}>Cancel</button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default EditStudentModal