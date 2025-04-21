import React, { createContext, useState, useEffect, ReactNode } from 'react';
import { User } from '../types/auth';

/*
    Share authentication status globally using react context,
    instead of passing down props through component tree.
*/

interface AuthContextType {
    isAuthenticated: boolean;
    user: User | null;
    token: string | null;
    login: (token: string, user: User) => void;
    logout: () => void;
}

// Create the context with a default value
export const AuthContext = createContext<AuthContextType>({
    isAuthenticated: false,
    user: null,
    token: null,
    login: () => {},
    logout: () => {},
});

interface AuthProviderProps  {
    children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
    // State to hold authentication data
    const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
    const [user, setUser] = useState<User | null>(null);
    const [token, setToken] = useState<string | null>(null);

    // Check if user is already authenticated on component mount
    useEffect(() => {
        const storedToken = localStorage.getItem('token');
        const storedUser = localStorage.getItem('user');

        if (storedToken && storedUser) {
            setToken(storedToken);
            setUser(JSON.parse(storedUser));
            setIsAuthenticated(true);
        }
    }, []);

    // Login function to set auth state
    const login = (token: string, user: User) => {
        localStorage.setItem('token', token);
        localStorage.setItem('user', JSON.stringify(user));

        setToken(token);
        setUser(user);
        setIsAuthenticated(true);
    }

    // Logout function to clear auth state
    const logout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');

        setToken(null);
        setUser(null);
        setIsAuthenticated(false);
    }

    // Provide the auth context to children components
    // Wrap rest of components in AuthProvider in App.tsx
    return (
        <AuthContext.Provider value={{ isAuthenticated, user, token, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

// Custom hook for context usage
export const useAuth = () => React.useContext(AuthContext);