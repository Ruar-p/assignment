export interface Message {
    id: string;
    senderId: string;
    receiverId: string;
    senderUsername: string;
    receiverUsername: string;
    content: string;
    timestamp: string;
    read: boolean;
}

export interface MessageRequest {
    receiverId: string;
    content: string;
}

export interface ChatUser {
    id: string;
    username: string;
    email: string;
}