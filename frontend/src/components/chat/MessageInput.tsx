import React, { useState } from 'react';
import { useChat } from '../../context/ChatContext';

const MessageInput: React.FC = () => {
    // Input field in component state and use global context
    // to hold selected user and send a message to them
    const [message, setMessage] = useState('');
    const { sendMessage, selectedUser } = useChat();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!message.trim() || !selectedUser) return;

        await sendMessage(message);
        setMessage('');
    };

    return (
        <div className="message-input">
            <form onSubmit={handleSubmit}>
                <input
                    type="text"
                    value={message}
                    onChange={(e) => setMessage(e.target.value)}
                    placeholder="Type a message..."
                    disabled={!selectedUser}
                />
                <button type="submit" disabled={!selectedUser || !message.trim()}>
                    Send
                </button>
            </form>
        </div>
    );
};

export default MessageInput;