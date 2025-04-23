import React from 'react';
import { useChat } from '../../context/ChatContext';

const UserDropdown: React.FC = () => {
    const { allUsers, selectUser, selectedUser } = useChat();

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
            <label>
                Start new conversation:
                <select
                    value={selectedUser?.id || ''}
                    onChange={handleChange}
                >
                    <option value="">Select a user</option>
                    {allUsers.map(user => (
                        <option key={user.id} value={user.id}>
                            {user.username}
                        </option>
                    ))}
                </select>
            </label>
        </div>
    );
};

export default UserDropdown