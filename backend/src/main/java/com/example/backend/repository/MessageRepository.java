package com.example.backend.repository;

import com.example.backend.model.Message;
import com.example.backend.model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    // Find all messages between two users (ordered by time)
    List<Message> findBySenderIdAndReceiverIdOrderByTimestampAsc(String senderId, String receiverId);

    // Find all messages  sent from one user to another
    List<Message> findByReceiverIdAndSenderIdOrderByTimestampAsc(String receiverId, String senderId);

    // Find unread messages for a user
    List<Message> findByReceiverIdAndReadFalseOrderByTimestampAsc(String receiverId);

    // Find recent conversations (for displaying chat list)
    List<Message> findBySenderIdOrReceiverIdOrderByTimestampDesc(String userId, String sameUser);

    // Received new messages after some timestamp
    List<Message> findByReceiverIdAndTimestampAfterOrderByTimestampAsc(String receiverId, LocalDateTime timestamp);

    // Sent new messages after some timestamp
    List<Message> findBySenderIdAndTimestampAfterOrderByTimestampAsc(String senderId, LocalDateTime timestamp);

}
