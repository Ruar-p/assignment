import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { authService } from '../services/api';
import { LoginRequest } from '../types/auth';
import { useAuth } from '../context/AuthContext';

const Login = () => {
    const [formData, setFormData] = useState<LoginRequest>({
        username: '',
        password: ''
    });

    const [isLoading, setIsLoading] = useState<boolean>(false);
    const { login } = useAuth();
    const [error, setError] = useState<string>('');
    const navigate = useNavigate();


    // Update form field state
    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    // Submit form data
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);
        setError('');

        try {
            const response = await authService.login(formData);

            // Use login function from AuthContext to update global state
            login(response.token, {
                id: response.userId,
                username: response.username,
                email: '' // Backend doesn't return email in the login response
            });

            navigate('/students');
        } catch (err) {
            setError('Invalid credentials');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="login-container">
            <h2>Login</h2>
            {error && <div className="error">{error}</div>}
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Username</label>
                    <input
                        type="text"
                        name="username"
                        value={formData.username}
                        onChange={handleChange}
                        required
                    />
                </div>
                <div>
                    <label>Password</label>
                    <input
                        type="password"
                        name="password"
                        value={formData.password}
                        onChange={handleChange}
                        required
                    />
                </div>
                <button type="submit" disabled={isLoading}>
                    {isLoading ? 'Logging in...' : 'Login'}
                </button>
            </form>
            <p>
                Don't have an account? <Link to="/register">Register</Link>
            </p>
        </div>
    );
};

export default Login;