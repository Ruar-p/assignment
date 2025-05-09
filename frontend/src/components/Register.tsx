import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { authService } from '../services/api';
import { RegisterRequest } from '../types/auth';

const Register = () => {
    const [formData, setFormData] = useState<RegisterRequest>({
        username: '',
        email: '',
        password: ''
    });
    
    const [error, setError] = useState<string>('');
    const navigate = useNavigate();
    const [isLoading, setIsLoading] = useState<boolean>(false);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);
        setError('');

        try {
            // Register user, but DON'T change auth state
            // No automatic login on registration
            await authService.register(formData);

            // Redirect to login page after successful registration
            navigate('/login');
        } catch (err) {
            setError('Registration failed');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="register-container">
            <h2>Register</h2>
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
                    <label>Email</label>
                    <input
                        type="email"
                        name="email"
                        value={formData.email}
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
                <button type="submit">Register</button>
            </form>
            <p>
                Already have an account? <Link to="/login">Login</Link>
            </p>
        </div>
    );
};

export default Register;