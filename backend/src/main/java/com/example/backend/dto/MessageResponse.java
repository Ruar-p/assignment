package com.example.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageResponse {
    private String id;
    private String senderId;
    private String receiverId;

    // For display purposes
    private String senderUsername;
    private String receiverUsername;

    private String content;
    private LocalDateTime timestamp;
    private boolean read;
}
