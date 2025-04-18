package com.example.backend.service;

import com.example.backend.model.Message;
import com.example.backend.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
Sending messages between users
Retrieving full conversations
Managing read/unread status
Getting a list of users someone has chatted with
 */
@Service
public class ChatService {
    private final MessageRepository messageRepository;
    private final UserService userService;

    @Autowired
    public ChatService(MessageRepository messageRepository, UserService userService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
    }

    // Send a message from a user to a different user
    public Message sendMessage(String senderId, String receiverId, String content) {
        // Check if invalid users
        userService.findById(senderId);
        userService.findById(receiverId);

        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        message.setRead(false);

        return messageRepository.save(message);
    }

    // Get message history between two users
    public List<Message> getConversation(String userId1, String userId2) {
        // TODO: Try the other repository method instead of doing the combining logic here
        // Get messages in both directions
        List<Message> sentMessages = messageRepository.findBySenderIdAndReceiverIdOrderByTimestampAsc(userId1, userId2);
        List<Message> receivedMessages = messageRepository.findBySenderIdAndReceiverIdOrderByTimestampAsc(userId2, userId1);

        // Combine and sort by timestamp
        return Stream.concat(sentMessages.stream(), receivedMessages.stream())
                .sorted(Comparator.comparing(Message::getTimestamp))
                .collect(Collectors.toList());

    }

    // Mark a message as read
    public Message markAsRead(String messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        message.setRead(true);
        return messageRepository.save(message);
    }

    // Mark all messages from receiver as read
    public void markAllAsRead(String senderId, String receiverId) {
        List<Message> unreadMessages = messageRepository.findByReceiverIdAndSenderIdOrderByTimestampAsc(receiverId, senderId);

        unreadMessages.forEach(message -> {
            message.setRead(true);
            messageRepository.save(message);
        });
    }

    // Get all unread messages for a user
    public List<Message> getUnreadMessages(String userId) {
        return messageRepository.findByReceiverIdAndReadFalseOrderByTimestampAsc(userId);
    }

    // Get list of user ids that have a conversation with current user
    public List<String> getChatUsersList(String userId) {
        // Get all messages involving this userId
        List<Message> allMessages = messageRepository.findBySenderIdOrReceiverIdOrderByTimestampDesc(userId, userId);

        // Extract unique user IDs (excluding current user)
        Set<String> chatUsers = new LinkedHashSet<>();  // Maintains insertion order

        for (Message message : allMessages) {
            if (message.getSenderId().equals(userId)) {
                chatUsers.add(message.getReceiverId());
            } else {
                chatUsers.add(message.getSenderId());
            }
        }

        return new ArrayList<>(chatUsers);
    }
}
