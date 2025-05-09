export interface User {
    id?: string;        // Optional for new User (MongoDB generates)
    username: string;
    email: string;
}

export interface LoginRequest {
    username: string;
    password: string;
}

export interface RegisterRequest {
    username: string;
    email: string;
    password: string;
}

export interface AuthResponse {
    token: string;
    userId: string;
    username: string;
}