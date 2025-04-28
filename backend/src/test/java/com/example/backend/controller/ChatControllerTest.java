package com.example.backend.controller;

import com.example.backend.dto.MessageRequest;
import com.example.backend.model.Message;
import com.example.backend.model.User;
import com.example.backend.service.ChatService;
import com.example.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)   // Fixes exception in markAsRead (UnnecessaryStubbingException)
public class ChatControllerTest {

    @Mock
    private ChatService chatService;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ChatController chatController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    // Test data
    private final String CURRENT_USER_ID = "user123";
    private final String CURRENT_USERNAME = "testuser";
    private final String OTHER_USER_ID = "user456";
    private final String MESSAGE_ID = "msg789";

    @BeforeEach
    void setUp() {
        // Configure ObjectMapper for LocalDateTime
        objectMapper.findAndRegisterModules();

        // Set up MockMvc
        mockMvc = MockMvcBuilders
                .standaloneSetup(chatController)
                .build();

        // Set up Authentication mock
        when(authentication.getName()).thenReturn(CURRENT_USERNAME);

        // Set up current user
        User currentUser = createTestUser(CURRENT_USER_ID, CURRENT_USERNAME, "test@example.com");
        when(userService.findByUsername(CURRENT_USERNAME)).thenReturn(currentUser);
    }

    @Test
    void sendMessage_ShouldCreateAndReturnNewMessage() throws Exception {
        // ARRANGE
        MessageRequest request = new MessageRequest();
        request.setReceiverId(OTHER_USER_ID);
        request.setContent("Hello, how are you?");

        Message sentMessage = createTestMessage(
                MESSAGE_ID,
                CURRENT_USER_ID,
                OTHER_USER_ID,
                request.getContent(),
                LocalDateTime.now(),
                false
        );

        when(chatService.sendMessage(
                eq(CURRENT_USER_ID),
                eq(OTHER_USER_ID),
                eq(request.getContent())
        )).thenReturn(sentMessage);

        // ACT & ASSERT
        mockMvc.perform(post("/api/chat/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(MESSAGE_ID)))
                .andExpect(jsonPath("$.senderId", is(CURRENT_USER_ID)))
                .andExpect(jsonPath("$.receiverId", is(OTHER_USER_ID)))
                .andExpect(jsonPath("$.content", is(request.getContent())))
                .andExpect(jsonPath("$.read", is(false)));

        // Verify service was called with correct params
        verify(chatService).sendMessage(CURRENT_USER_ID, OTHER_USER_ID, request.getContent());
    }

    @Test
    void getConversation_ShouldReturnMessages() throws Exception {
        // ARRANGE
        List<Message> conversation = Arrays.asList(
                createTestMessage("msg1", CURRENT_USER_ID, OTHER_USER_ID, "Hello", LocalDateTime.now().minusHours(1), true),
                createTestMessage("msg2", OTHER_USER_ID, CURRENT_USER_ID, "Hi there", LocalDateTime.now(), false)
        );

        when(chatService.getConversation(CURRENT_USER_ID, OTHER_USER_ID)).thenReturn(conversation);

        // Mock other user for username lookup
        User otherUser = createTestUser(OTHER_USER_ID, "otheruser", "other@example.com");
        when(userService.findById(OTHER_USER_ID)).thenReturn(otherUser);

        // ACT & ASSERT
        mockMvc.perform(get("/api/chat/conversation/{userId}", OTHER_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("msg1")))
                .andExpect(jsonPath("$[0].senderId", is(CURRENT_USER_ID)))
                .andExpect(jsonPath("$[0].receiverId", is(OTHER_USER_ID)))
                .andExpect(jsonPath("$[0].content", is("Hello")))
                .andExpect(jsonPath("$[0].read", is(true)))
                .andExpect(jsonPath("$[1].id", is("msg2")))
                .andExpect(jsonPath("$[1].senderId", is(OTHER_USER_ID)))
                .andExpect(jsonPath("$[1].receiverId", is(CURRENT_USER_ID)))
                .andExpect(jsonPath("$[1].content", is("Hi there")))
                .andExpect(jsonPath("$[1].read", is(false)));

        // Verify messages were marked as read
        verify(chatService).markAllAsRead(OTHER_USER_ID, CURRENT_USER_ID);
    }

    @Test
    void markAsRead_ShouldUpdateAndReturnMessage() throws Exception {
        // ARRANGE
        Message message = createTestMessage(
                MESSAGE_ID,
                OTHER_USER_ID,
                CURRENT_USER_ID,
                "Test message",
                LocalDateTime.now(),
                true  // Now marked as read
        );

        when(chatService.markAsRead(MESSAGE_ID)).thenReturn(message);

        // Mock users for username lookup
        User otherUser = createTestUser(OTHER_USER_ID, "otheruser", "other@example.com");
        when(userService.findById(OTHER_USER_ID)).thenReturn(otherUser);

        // ACT & ASSERT
        mockMvc.perform(post("/api/chat/mark-read/{messageId}", MESSAGE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(MESSAGE_ID)))
                .andExpect(jsonPath("$.read", is(true)));

        // Verify service was called
        verify(chatService).markAsRead(MESSAGE_ID);
    }

    @Test
    void getUnreadMessages_ShouldReturnUnreadMessages() throws Exception {
        // ARRANGE
        List<Message> unreadMessages = Arrays.asList(
                createTestMessage("msg1", OTHER_USER_ID, CURRENT_USER_ID, "Hello", LocalDateTime.now(), false),
                createTestMessage("msg2", OTHER_USER_ID, CURRENT_USER_ID, "Are you there?", LocalDateTime.now().plusMinutes(5), false)
        );

        when(chatService.getUnreadMessages(CURRENT_USER_ID)).thenReturn(unreadMessages);

        // Mock other user for username lookup
        User otherUser = createTestUser(OTHER_USER_ID, "otheruser", "other@example.com");
        when(userService.findById(OTHER_USER_ID)).thenReturn(otherUser);

        // ACT & ASSERT
        mockMvc.perform(get("/api/chat/unread")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("msg1")))
                .andExpect(jsonPath("$[0].content", is("Hello")))
                .andExpect(jsonPath("$[0].read", is(false)))
                .andExpect(jsonPath("$[1].id", is("msg2")))
                .andExpect(jsonPath("$[1].content", is("Are you there?")))
                .andExpect(jsonPath("$[1].read", is(false)));
    }

    @Test
    void getChatUsers_ShouldReturnUserList() throws Exception {
        // ARRANGE
        List<String> userIds = Arrays.asList(OTHER_USER_ID);
        when(chatService.getChatUsersList(CURRENT_USER_ID)).thenReturn(userIds);

        // Mock other user for lookup
        User otherUser = createTestUser(OTHER_USER_ID, "otheruser", "other@example.com");
        when(userService.findById(OTHER_USER_ID)).thenReturn(otherUser);

        // ACT & ASSERT
        mockMvc.perform(get("/api/chat/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(OTHER_USER_ID)))
                .andExpect(jsonPath("$[0].username", is("otheruser")))
                .andExpect(jsonPath("$[0].email", is("other@example.com")));
    }

    @Test
    void pollNewMessages_ShouldReturnNewMessages() throws Exception {
        // ARRANGE
        LocalDateTime since = LocalDateTime.now().minusMinutes(10);
        String timestamp = since.toString();

        List<Message> newMessages = Collections.singletonList(
                createTestMessage("msg3", OTHER_USER_ID, CURRENT_USER_ID, "New message", LocalDateTime.now(), false)
        );

        when(chatService.getNewMessages(eq(CURRENT_USER_ID), any(LocalDateTime.class)))
                .thenReturn(newMessages);

        // Mock other user for username lookup
        User otherUser = createTestUser(OTHER_USER_ID, "otheruser", "other@example.com");
        when(userService.findById(OTHER_USER_ID)).thenReturn(otherUser);

        // ACT & ASSERT
        mockMvc.perform(get("/api/chat/poll")
                        .param("timestamp", timestamp)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("msg3")))
                .andExpect(jsonPath("$[0].content", is("New message")))
                .andExpect(jsonPath("$[0].read", is(false)));
    }

    // Helper method to create test users
    private User createTestUser(String id, String username, String email) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        return user;
    }

    // Helper method to create test messages
    private Message createTestMessage(String id, String senderId, String receiverId,
                                      String content, LocalDateTime timestamp, boolean read) {
        Message message = new Message();
        message.setId(id);
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setTimestamp(timestamp);
        message.setRead(read);
        return message;
    }
}