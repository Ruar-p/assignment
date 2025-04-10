import React, { useState } from 'react';
import { CreateStudentRequest, COURSE_OPTIONS } from '../../types/student';

interface AddStudentModalProps {
    isOpen: boolean;
    onClose: () => void;
    onSubmit: (student: CreateStudentRequest) => void;
}

function AddStudentModal({ isOpen, onClose, onSubmit }: AddStudentModalProps) {
    const [formData, setFormData] = useState<CreateStudentRequest>({
        name: '',
        courses: [],
        phoneNumber: '',
        dateOfBirth: ''
    });

    if (!isOpen) return <></>;

    // Define what happens when form changes, courses change, or the form is submitted
    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleCoursesChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        // Convert HTMLSelectElement selected options to array
        const selectedOption = Array.from(e.target.selectedOptions, option => option.value);
        setFormData({ ...formData, courses: selectedOption});
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        onSubmit(formData);
        setFormData({ name: '', courses: [], phoneNumber: '', dateOfBirth: '' });
    };

    return (
        <div>
            <div className="modal-overlay">
                <div className="modal-content">
                    <h2>Add New Student</h2>
                    <form onSubmit={handleSubmit}>
                        <div className="form-group">
                            <label htmlFor="name">Name:</label>
                            <input
                                type="text"
                                id="name"
                                name="name"
                                value={formData.name}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="courses">Courses:</label>
                            <select
                                id="courses"
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
                        <label htmlFor="phoneNumber">Phone Number:</label>
                            <input
                                type="tel"
                                id="phoneNumber"
                                name="phoneNumber"
                                pattern="[0-9]{10}"
                                placeholder="10 digits, no spaces or dashes"
                                value={formData.phoneNumber}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="dateOfBirth">Date of Birth:</label>
                            <input
                                type="date"
                                id="dateOfBirth"
                                name="dateOfBirth"
                                value={formData.dateOfBirth}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="modal-actions">
                            <button type="submit">Add Student</button>
                            <button type="button" onClick={onClose}>Cancel</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
};


export default AddStudentModal;