import React from 'react';
import { useChat } from '../../context/ChatContext';

const UserList: React.FC = () => {
    const { users, selectedUser, selectUser, unreadCounts } = useChat();

    // Kind of gross conditional formatting
    // But basically:
    // - Either display no conversations OR
    // - Display a list of users with their unique id as key (one will have a custom highlight if selected)
    // - Clicking on one of the entries will update the selected user in the chat context
    // - Username of the user and number of unread messages gets displayed next to that user
    return (
        <div className="user-list">
            <h2>Conversations</h2>
            {users.length === 0 ? (
                <p>No Conversations yet</p>
            ) : (
                <ul>
                    {users.map(user => (
                        <li
                            key={user.id}
                            className={selectedUser?.id === user.id ? 'selected' : ''}
                            onClick={() => selectUser(user)}
                        >
                            <span className="username">{user.username}</span>
                            {unreadCounts.get(user.id) && (
                                <span className="unread-badge">
                                    {unreadCounts.get(user.id)}
                                </span>
                            )}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default UserList;