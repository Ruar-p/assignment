export interface Student {
    id?: string;            // Optional for new students (MongoDB generates)
    name: string;
    courses: string[];
    phoneNumber: string;
    dateOfBirth: string;    // ISO format
}

export interface CreateStudentRequest {
    name: string;
    courses: string[];
    phoneNumber: string;
    dateOfBirth: string;
}

export interface UpdateStudentRequest {
    name: string;
    courses: string[];    
    phoneNumber: string;
    dateOfBirth: string;
}

// Available course options for dropdown
export const COURSE_OPTIONS = [
    "Computer Science",
    "Mathematics",
    "Physics",
    "Biology",
    "Chemistry",
    "History",
    "Literature"
];