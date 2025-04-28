
/*
JUnit 5: Provides the test framework (assertions, test lifecycle)
Mockito: Mocks dependencies (services that controllers call)
MockMvc: Spring's utility to simulate HTTP requests without starting a server
Spring Security Test: Helps test authenticated endpoints
 */

package com.example.backend.controller;


import com.example.backend.dto.CreateStudentRequest;
import com.example.backend.dto.UpdateStudentRequest;
import com.example.backend.model.Student;
import com.example.backend.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)     // For @Mock annotations
public class StudentControllerTest {
    // Mock student service
    @Mock
    private StudentService studentService;

    // Inject StudentService into the controller
    @InjectMocks
    private StudentController studentController;


    // For simulating HTTP requests
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    // Before each test
    @BeforeEach
    void setUp() {
        // Configure ObjectMapper to handle Java 8 date types
        objectMapper.findAndRegisterModules();

        mockMvc = MockMvcBuilders
                .standaloneSetup(studentController) // Don't load the entire Spring context (faster tests)
                .build();
    }


    // TESTS //
    @Test
    void getAllStudents_ShouldReturnListOfStudents() throws Exception {
        // ARRANGE: Set up test data
        List<Student> students = Arrays.asList(
                createTestStudent("1", "Alice Smith", List.of("Math", "Science"), "1234567890"),
                createTestStudent("2", "Bob Jones", List.of("History", "English"), "0987654321")
        );

        // Tell the mock service to return this data when getAllStudents() is called
        when(studentService.getAllStudents()).thenReturn(students);

        // ACT & ASSERT: Perform the request and verify the response
        mockMvc.perform(get("/api/students")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("1")))
                .andExpect(jsonPath("$[0].name", is("Alice Smith")))
                .andExpect(jsonPath("$[1].id", is("2")))
                .andExpect(jsonPath("$[1].name", is("Bob Jones")));
    }

    @Test
    void getStudentById_ShouldReturnStudent() throws Exception {
        // ARRANGE
        Student student = createTestStudent("1", "Alice Smith", List.of("Math", "Science"), "1234567890");

        when(studentService.findById(student.getId())).thenReturn(student);

        // ACT & ASSERT
        mockMvc.perform(get("/api/students/{id}", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.name", is("Alice Smith")))
                .andExpect(jsonPath("$.courses[0]", is("Math")))
                .andExpect(jsonPath("$.courses[1]", is("Science")))
                .andExpect(jsonPath("$.phoneNumber", is("1234567890")));
    }

    @Test
    void addStudent_ShouldCreateAndReturnNewStudent() throws Exception {
        // ARRANGE
        CreateStudentRequest request = new CreateStudentRequest();
        request.setName("Alice Smith");
        request.setCourses(List.of("Math", "Science"));
        request.setPhoneNumber("1234567890");
        request.setDateOfBirth(LocalDate.of(2000, 1, 15));

        Student savedStudent = new Student();
        savedStudent.setId("3");
        savedStudent.setName(request.getName());
        savedStudent.setCourses(request.getCourses());
        savedStudent.setPhoneNumber(request.getPhoneNumber());
        savedStudent.setDateOfBirth(request.getDateOfBirth());

        when(studentService.addStudent(any(Student.class))).thenReturn(savedStudent);

        // ACT & ASSERT
        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())    // 201
                .andExpect(jsonPath("$.id", is("3")))
                .andExpect(jsonPath("$.name", is("Alice Smith")))
                .andExpect(jsonPath("$.courses[0]", is("Math")))
                .andExpect(jsonPath("$.courses[1]", is("Science")))
                .andExpect(jsonPath("$.phoneNumber", is("1234567890")));


    }

    @Test
    void updateStudent_ShouldUpdateAndReturnStudent() throws Exception {
        // ARRANGE
        String studentId = "4";

        // Create update request
        UpdateStudentRequest request = new UpdateStudentRequest();
        request.setName("Alice Smith");
        request.setCourses(List.of("Art", "Music"));
        request.setPhoneNumber("1234567890");
        request.setDateOfBirth(LocalDate.of(2001, 5, 20));

        // Create existing student that will be found
        Student existingStudent = new Student();
        existingStudent.setId(studentId);
        existingStudent.setName("Alice Old-Name");
        // Other properties...

        // Create updated student that will be returned
        Student updatedStudent = new Student();
        updatedStudent.setId(studentId);
        updatedStudent.setName(request.getName());
        updatedStudent.setCourses(request.getCourses());
        updatedStudent.setPhoneNumber(request.getPhoneNumber());
        updatedStudent.setDateOfBirth(request.getDateOfBirth());

        // Mock service calls
        when(studentService.findById(studentId)).thenReturn(existingStudent);
        when(studentService.editStudent(any(Student.class))).thenReturn(updatedStudent);

        // ACT & ASSERT
        mockMvc.perform(put("/api/students/{id}", studentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(studentId)))
                .andExpect(jsonPath("$.name", is("Alice Smith")))
                .andExpect(jsonPath("$.courses[0]", is("Art")))
                .andExpect(jsonPath("$.phoneNumber", is("1234567890")));
    }

    @Test
    void deleteStudent_WithValidId_ShouldReturnNoContent() throws Exception {
        // ARRANGE
        String studentId = "5";
        Student existingStudent = createTestStudent(studentId, "Alice Smith",
                List.of("Biology"), "1234567890");

        when(studentService.findById(studentId)).thenReturn(existingStudent);
        doNothing().when(studentService).deleteStudent(studentId);

        // ACT & ASSERT
        mockMvc.perform(delete("/api/students/{id}", studentId))
                .andExpect(status().isNoContent());  // 204 No Content
    }

    @Test
    void getStudentById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // ARRANGE
        when(studentService.findById("999"))
                .thenThrow(new RuntimeException("Student not found"));

        // ACT & ASSERT
        mockMvc.perform(get("/api/students/{id}", "999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStudent_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // ARRANGE
        String nonExistentId = "999";
        UpdateStudentRequest request = new UpdateStudentRequest();
        request.setName("Nobody");
        request.setCourses(List.of("None"));

        when(studentService.findById(nonExistentId))
                .thenThrow(new RuntimeException("Student not found"));

        // ACT & ASSERT
        mockMvc.perform(put("/api/students/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteStudent_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // ARRANGE
        String nonExistentId = "999";

        when(studentService.findById(nonExistentId))
                .thenThrow(new RuntimeException("Student not found"));

        // ACT & ASSERT
        mockMvc.perform(delete("/api/students/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }




    // Helper method to create test students
    private Student createTestStudent(String id, String name, List<String> courses, String phone) {
        Student student = new Student();
        student.setId(id);
        student.setName(name);
        student.setCourses(courses);
        student.setPhoneNumber(phone);
        student.setDateOfBirth(LocalDate.now().minusYears(20)); // Default dob
        return student;
    }


}
