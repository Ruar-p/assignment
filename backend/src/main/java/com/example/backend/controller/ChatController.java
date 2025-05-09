package com.example.backend.controller;

import com.example.backend.dto.MessageRequest;
import com.example.backend.dto.MessageResponse;
import com.example.backend.dto.UserResponse;
import com.example.backend.model.Message;
import com.example.backend.model.User;
import com.example.backend.service.ChatService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/*
    Chat Endpoints

    Authentication is added to identify the specific
    user that is making the request, which is not the same situation
    as the StudentController at the moment. If access to students will
    be restricted depending on user authentication, those endpoints will
    need to change.
 */

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;
    private final UserService userService;

    @Autowired
    public ChatController(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }

    // Send a message to another user
    @PostMapping("/send")
    public ResponseEntity<MessageResponse> sendMessage(
            Authentication authentication,
            @RequestBody MessageRequest request) {

        // Get current user ID from JWT authentication
        String currentUsername = authentication.getName();
        User currentUser = userService.findByUsername(currentUsername);

        // Send message
        Message message = chatService.sendMessage(
                currentUser.getId(),
                request.getReceiverId(),
                request.getContent()
        );

        // Convert response to DTO
        MessageResponse response = convertToDto(message);
        return ResponseEntity.ok(response);
    }

    // Get conversation with another user
    @GetMapping("/conversation/{userId}")
    public ResponseEntity<List<MessageResponse>> getConversation(
            Authentication authentication,
            @PathVariable String userId) {

        // Get current user ID from JWT authentication
        String currentUsername = authentication.getName();
        User currentUser = userService.findByUsername(currentUsername);

        // Mark messages from other user as read
        chatService.markAllAsRead(userId, currentUser.getId());

        // Get conversation
        List<Message> messages = chatService.getConversation(currentUser.getId(), userId);

        // Convert to response DTOs
        List<MessageResponse> response = messages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // Mark a message as read
    @PostMapping("/mark-read/{messageId}")
    public ResponseEntity<MessageResponse> markAsRead(
            Authentication authentication,
            @PathVariable String messageId) {

        // Mark message as read
        Message message = chatService.markAsRead(messageId);

        // Convert to dto
        MessageResponse response = convertToDto(message);
        return ResponseEntity.ok(response);
    }

    // Get unread messages
    @GetMapping("/unread")
    public ResponseEntity<List<MessageResponse>> getUnreadMessages(
            Authentication authentication) {

        // Get current ID from JWT authentication
        String currentUsername = authentication.getName();
        User currentUser = userService.findByUsername(currentUsername);

        // Get unread messages
        List<Message> messages = chatService.getUnreadMessages(currentUser.getId());

        // Convert to response DTOs
        List<MessageResponse> response = messages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // Get list of users that current user has chatted with
    // TODO: Maybe move this to UserController instead?
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getChatUser(
            Authentication authentication) {
        // Get current user ID from JWT authentication
        String currentUsername = authentication.getName();
        User currentUser = userService.findByUsername(currentUsername);

        // Get list of users that chatted with the selected user
        List<String> userIds = chatService.getChatUsersList(currentUser.getId());

        // Convert IDs to User objects
        List<User> users = new ArrayList<>();
        for (String userId : userIds) {
            try {
                users.add(userService.findById(userId));
            } catch (Exception e) {
                // Skip users that may have been deleted
            }
        }

        // Then to dto list
        List<UserResponse> response = users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // Poll for new sent or received messages
    @GetMapping("/poll")
    public ResponseEntity<List<MessageResponse>> pollNewMessages(
            Authentication authentication,
            @RequestParam String timestamp) {

        // Get current user ID from JWT authentication
        String currentUsername = authentication.getName();
        User currentUser = userService.findByUsername(currentUsername);

        // Parse timestamp
        LocalDateTime since = LocalDateTime.parse(timestamp);

        // Get new messages
        List<Message> newMessages = chatService.getNewMessages(currentUser.getId(), since);

        // Convert response into DTOs
        List<MessageResponse> response = newMessages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }



    // Helper method to convert Message entity to MessageResponse DTO
    private MessageResponse convertToDto(Message message) {
        MessageResponse dto = new MessageResponse();
        dto.setId(message.getId());
        dto.setSenderId(message.getSenderId());
        dto.setReceiverId(message.getReceiverId());
        dto.setContent(message.getContent());
        dto.setTimestamp(message.getTimestamp());
        dto.setRead(message.isRead());

        // Add usernames for convenience
        try {
            User sender = userService.findById(message.getSenderId());
            User receiver = userService.findById(message.getReceiverId());
            dto.setSenderUsername(sender.getUsername());
            dto.setReceiverUsername(receiver.getUsername());
        } catch (Exception e) {
            // TODO: Handle case where a user might be deleted
        }

        return dto;
    }

    // Helper method to convert User entity to UserResponse DTO
    private UserResponse convertToDto(User user) {
        UserResponse dto = new UserResponse();

        // Might want to change which properties are shared depending on
        // security needs of the chat.
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());

        return dto;
    }
}
