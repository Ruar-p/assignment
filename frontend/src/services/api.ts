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
    },

    logout: async (): Promise<void> => {
        // Here we would get rid of stored tokens. I don't think this is applicable to this setup right now.
        localStorage.removeItem('token');
        localStorage.removeItem('user');

        // Here we would call the logout endpoint if the backend tracked the session
        // await axios.post(`${API_URL}/auth/logout`);

        // Return a resolved promise
        return Promise.resolve();
    }
};