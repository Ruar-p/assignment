import { Student, CreateStudentRequest, UpdateStudentRequest } from "../types/student";
import api from './axiosConfig'; // Instead of regular axios, using the api with the jwt stuff


const API_URL = '/students';

export const studentService = {
    // Get all students
    getAll: async (): Promise<Student[]> => {
        const response = await api.get(API_URL);
        return response.data;
    },

    // Get student by ID
    getById: async (id: string): Promise<Student> => {
        const response = await api.get(`${API_URL}/${id}`);
        return response.data;
    },

    // Create new student
    createNewStudent: async(student: CreateStudentRequest): Promise<Student> => {
        const response = await api.post(API_URL, student);
        return response.data;
    },

    // Update existing student
    update: async (id: string, student: UpdateStudentRequest): Promise<Student> => {
        const response = await api.put(`${API_URL}/${id}`, student);
        return response.data;
    },

    // Delete student
    delete: async (id: string): Promise<void> => {
        await api.delete(`${API_URL}/${id}`);

    }
};