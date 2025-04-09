import axios from 'axios';
import { LoginRequest, RegisterRequest, User } from '../types/auth';

const API_URL = '/api';

export const authService = {
    login: async (data: LoginRequest): Promise<string> => {
        const response = await axios.post(`${API_URL}/auth/login`, data);
        return response.data;
    },

    register: async (data: RegisterRequest): Promise<User> => {
        const response = await axios.post(`${API_URL}/auth/register`, data);
        return response.data;
    }
};