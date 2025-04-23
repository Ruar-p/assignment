import React from 'react';
import { useChat } from '../../context/ChatContext';
import { useAuth } from '../../context/AuthContext';

const UserDropdown: React.FC = () => {
    const { allUsers, selectUser, selectedUser } = useChat();
    const { user : currentUser } = useAuth(); // Get current user to filter them from dropdown

    const availableUsers = allUsers.filter(user => user.id !== currentUser?.id);    // Filter out current user

    const handleChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const userId = e.target.value;
        if (!userId) return;

        const user = allUsers.find(user => user.id === userId);
        if (user) {
            selectUser(user);
        }
    };

    return (
        <div className="user-dropdown">
            <label>Start new conversation:</label>
            <select
                    value={selectedUser?.id || ''}
                    onChange={handleChange}
                >
                    <option value="">Select a user</option>
                    {availableUsers.map(user => (
                        <option key={user.id} value={user.id}>
                            {user.username}
                        </option>
                    ))}
            </select>
        </div>
    );
};

export default UserDropdown