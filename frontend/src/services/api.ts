import { LoginRequest, RegisterRequest, AuthResponse } from '../types/auth';

// Custom axios config to be consistent with studentApi
// Saves the token and user
import api from './axiosConfig'; 

/*
    API for interfacing with backend (login, register, logout).
    baseUrl = /api/
*/


export const authService = {
    login: async (data: LoginRequest): Promise<AuthResponse> => {
        const response = await api.post(`auth/login`, data);
        return response.data;
    },

    register: async (data: RegisterRequest): Promise<AuthResponse> => {
        const response = await api.post(`auth/register`, data);
        return response.data;
    },

    logout: async (): Promise<void> => {
        // Clear local storage (actual removal happens in context)
        // No backend call necessary
        return Promise.resolve();
    }
};