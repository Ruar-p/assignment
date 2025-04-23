import api from './axiosConfig';
import { ChatUser } from '../types/chat';

const API_URL = '/users'

export const userService = {
    // Simply retrieve all users
    getAllChatUsers: async (): Promise<ChatUser[]> => {
        const response = await api.get(`${API_URL}`);
        return response.data;
    }
};