import React from 'react';
import { ChatProvider } from '../../context/ChatContext';
import UserList from './UserList';
import UserDropdown from './UserDropdown';
import MessageList from './MessageList';
import MessageInput from './MessageInput';
import { useAuth } from '../../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import './ChatPage.css';

const ChatPage: React.FC = () => {
    const { logout } = useAuth();   //
    const navigate = useNavigate();

    // Update the auth context and navigate to login screen on logout
    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <ChatProvider>
            <div className="chat-page">
                <div className="header">
                    <h1>Chat Application</h1>
                    <div className="actions">
                        <button onClick={() => navigate('/students')}>Manage Students</button>
                        <button onClick={handleLogout} className="logout-button">Logout</button>
                    </div>
                </div>

                <div className="chat-container">
                    <UserDropdown />
                    <UserList />
                    <div className="chat-main">
                        <MessageList />
                        <MessageInput />
                    </div>
                </div>
            </div>
        </ChatProvider>
    );
};

export default ChatPage;

