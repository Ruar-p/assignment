import api from './axiosConfig';
import { Message, MessageRequest, ChatUser } from '../types/chat';

const API_URL = '/chat';

export const chatService = {
    // Send a message to another user
    sendMessage: async (request: MessageRequest): Promise<Message> => {
        const response = await api.post(`${API_URL}/send`, request);
        return response.data
    },

    // Get conversation with another user
    getConversation: async (userId: string): Promise<Message[]> => {
        const response = await api.get(`${API_URL}/conversation/${userId}`);
        return response.data;
    },

    // Mark a message as read
    markAsRead: async (messageId: string): Promise<Message> => {
        const response = await api.post(`${API_URL}/mark-read/${messageId}`);
        return response.data;
    },

    // Get all unread messages
    getUnreadMessages: async (): Promise<Message[]> => {
        const response = await api.get(`${API_URL}/unread`);
        return response.data;
    },

    // Get users that current user has chatted with (user identified based on auth key)
    getChatUsers: async (): Promise<ChatUser[]> => {
        const response = await api.get(`${API_URL}/users`);
        return response.data;
    },

    // Poll for new messages since a timestamp
    pollNewMessages: async (timestamp: string): Promise<Message[]> => {
        const response = await api.get(`${API_URL}/poll?timestamp=${timestamp}`);
        return response.data;
    }
};