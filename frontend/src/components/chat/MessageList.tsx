import React, { useRef, useEffect } from 'react';
import { useChat } from '../../context/ChatContext';
import { useAuth } from '../../context/AuthContext';

const MessageList: React.FC = () => {
    const { messages, selectedUser } = useChat();
    const { user} = useAuth();

    if (!selectedUser) {
        return (
            <div className="message-list empty-state">
                <p>Select a user to start chatting</p>
            </div>
        );
    }

    // Message list
    // Header section
    // Message section
    // - No messages OR
    // - Map all messages from chat context to separate message divs
    // - A message div has: 
    //      -- a section saying if the message was sent or received
    //      -- a section displaying the content
    //      -- a section with the timestamp
    //      -- a section with read status

    return (
        <div className="message-list">
            <div className="chat-header">
                <h3>Chat with {selectedUser.username}</h3>
            </div>

            <div className="messages">
                {messages.length === 0 ? (
                    <p className="no-messages">No messages yet. Say hello!</p>
                ) : (
                    messages.map(message => (
                        <div
                            key={message.id}
                            className={`message ${message.senderId === user?.id ? 'sent' : 'received'}`}
                        >
                            <div className="message-content">
                                {message.content}
                            </div>
                            <div className="message-timestamp">
                                {new Date(message.timestamp).toLocaleTimeString()}
                                {message.senderId !== user?.id && (
                                    <span className={`read-status ${message.read ? 'read' : 'unread'}`}>
                                        {message.read ? 'XX' : 'X'}
                                    </span>
                                )}
                            </div>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
};

export default MessageList;