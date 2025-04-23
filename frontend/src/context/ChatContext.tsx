import React, { createContext, useState, useEffect, useContext } from 'react';
import { Message, ChatUser } from '../types/chat';
import { chatService } from '../services/chatApi';
import { userService } from '../services/userApi';
import { useAuth } from './AuthContext';

// Define format
interface ChatContextType {
    users: ChatUser[];
    allUsers: ChatUser[];
    selectedUser: ChatUser | null;
    messages: Message[];
    unreadCounts: Map<string, number>;
    selectUser: (user: ChatUser) => void;
    sendMessage: (content: string) => Promise<void>;
    refreshMessages: () => Promise<void>;
}

// Create the context with a default value
const ChatContext = createContext<ChatContextType>({
    users: [],
    allUsers: [],
    selectedUser: null,
    messages: [],
    unreadCounts: new Map(),
    selectUser: () => {},
    sendMessage: async () => {},
    refreshMessages: async () => {}
});



export const ChatProvider: React.FC<{children: React.ReactNode}> = ({ children }) => {
    const { user: currentUser } = useAuth();                                            
    const [users, setUsers] = useState<ChatUser[]>([]);                                 // List of users that have chatted with current user
    const [allUsers, setAllUsers] = useState<ChatUser[]>([]);                           // List of ALL users that are in the system
    const [selectedUser, setSelectedUser] = useState<ChatUser | null>(null);            // Selected user
    const [messages, setMessages] = useState<Message[]>([]);                            // List of messages
    const [unreadCounts, setUnreadCounts] = useState<Map<string, number>>(new Map());   // Read message count

    // The toISOString method adds a letter to the end denoting timezone
    // In this case it is Z
    // I am just stripping it everywhere for now
    const [lastPoll, setLastPoll] = useState<string>(new Date().toISOString().replace('Z', ''));         // Most recent poll time

    // Load chat users on mount
    useEffect(() => {
        if (currentUser) {
            // TODO: Probably just change it to load all users and select from all users
            // However, there might be a problem if userbase becomes massive and there is 3 second polling....
            loadChatUsers();
            loadAllChatUsers();
            loadUnreadMessages();

            // Set up polling for new messages
            const interval = setInterval(pollMessages, 3000); // 3 seconds between poll?
            return () => clearInterval(interval);
        }
    }, [currentUser]); // Run every time current user changes


    // Load ONLY users that have chatted with current user
    const loadChatUsers = async () => {
        try {
            const chatUsers = await chatService.getChatUsers();
            setUsers(chatUsers);
        } catch (error) {
            console.error('Failed to load chat users', error);
        }
    };

    // Load ALL users (for initial chat dropdown)
    const loadAllChatUsers = async () => {
        try {
            const allChatUsers = await userService.getAllChatUsers();
            setAllUsers(allChatUsers);
        } catch (error) {
            console.error('Failed to load all users', error);
        }
    };

    // Load unread messages and update counts
    const loadUnreadMessages = async () => {
        try {
            const unreadMessages = await chatService.getUnreadMessages();   // API call for unread messages

            // Count unread messages per sender
            const counts = new Map<string, number>();
            unreadMessages.forEach(msg => {
                const count = counts.get(msg.senderId) || 0;
                counts.set(msg.senderId, count + 1);
            });
            setUnreadCounts(counts);

        } catch (error) {
            console.error('Failed to load unread messages', error);
        }
    };

    // Poll for new messages
    const pollMessages = async () => {
        try {

            const newMessages = await chatService.pollNewMessages(lastPoll.replace('Z', ''));    // Use API to get messages newer than last polling time stored on front end
            setLastPoll(new Date().toISOString().replace('Z', ''));                              // Update last polling time

            // Go through new message list if it is not empty
            if (newMessages.length > 0) {
                // If a user is selected, add new messages to conversation
                if (selectedUser &&                         // if a user is selected AND
                    newMessages.some(msg =>                 // the new message is either from selected user OR received by selected user
                        msg.senderId === selectedUser.id ||     
                        msg.receiverId === selectedUser.id      
                    )) {
                        refreshMessages();                  // then we refresh the messages
                    }

                    // Update unread counts
                    loadUnreadMessages();
            }
        } catch (error) {
            console.error('Failed to poll messages', error);
        }
    };

    // Select a user to chat with
    const selectUser = async (user: ChatUser) => {
        setSelectedUser(user);  // Update state of selected user
        if (user) {
            try {
                const conversation = await chatService.getConversation(user.id);    // Use api to update messages/conversation state
                setMessages(conversation);
                
                // Update unread counts
                loadUnreadMessages();

                // Also refresh chat users list to include this user if they're not already there
                loadChatUsers();
            } catch (error) {
                console.error('Failed to load conversation', error);
            }
        } else {
            setMessages([]); // No user selected, so messages are blank
        }
    };

    // Send a message to the selected user
    const sendMessage = async (content: string) => {
        if (!selectedUser) return;  // No user selected, do nothing

        try {
            await chatService.sendMessage({     // API attempts to send message using info
                receiverId: selectedUser.id,
                content
            }); 

            // Refresh messages to show the new message
            refreshMessages();

            // After sending first message, refresh the chat users list
            await loadChatUsers();
        } catch (error) {
            console.error('Failed to send message', error);
        }
    };

    // Refresh current conversation
    // TODO: Double check if
    // 2 api calls each poll - getConversation, pollNewMessages()
    // is actually necessary
    const refreshMessages = async () => {
        if (!selectedUser) return;

        try {
            const conversation = await chatService.getConversation(selectedUser.id);    // Just API call and update the message state
            setMessages(conversation);
        } catch (error) {
            console.error('Failed to refresh messages', error);
        }
    };

    return (
        <ChatContext.Provider
            value={{
                users,
                allUsers,
                selectedUser,
                messages,
                unreadCounts,
                selectUser,
                sendMessage,
                refreshMessages,
            }}
        >
            {children}
        </ChatContext.Provider>
    );
};

// Custom hook for context usage
export const useChat = () => useContext(ChatContext);