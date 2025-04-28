package com.example.backend.controller;

import com.example.backend.dto.CreateStudentRequest;
import com.example.backend.dto.StudentResponse;
import com.example.backend.dto.UpdateStudentRequest;
import com.example.backend.model.Student;
import com.example.backend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public ResponseEntity<StudentResponse> addStudent(@RequestBody CreateStudentRequest createStudentRequest) {
        // Convert DTO to Entity
        Student student = new Student();
        student.setName(createStudentRequest.getName());
        student.setCourses(createStudentRequest.getCourses());
        student.setPhoneNumber(createStudentRequest.getPhoneNumber());
        student.setDateOfBirth(createStudentRequest.getDateOfBirth());

        // Save student
        Student savedStudent = studentService.addStudent(student);

        // Convert entity to response DTO
        StudentResponse response = convertToDto(savedStudent);

        return ResponseEntity.created(URI.create("/api/students/" + savedStudent.getId()))
                                        .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable String id) {
        try {
            Student student = studentService.findById(id);

            // Convert to DTO
            StudentResponse response = convertToDto(student);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

    }

    @GetMapping
    public ResponseEntity<List<StudentResponse>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();

        // Convert to list of DTO
        List<StudentResponse> response = students.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> updateStudent(@PathVariable String id,
                                                         @RequestBody UpdateStudentRequest updateRequest) {
        try {
            // Verify existence of student with provided id
            Student existingStudent = studentService.findById(id);

            // Update fields
            existingStudent.setName(updateRequest.getName());
            existingStudent.setCourses(updateRequest.getCourses());
            existingStudent.setPhoneNumber(updateRequest.getPhoneNumber());
            existingStudent.setDateOfBirth(updateRequest.getDateOfBirth());

            // Save updated student
            Student updatedStudent = studentService.editStudent(existingStudent);

            // Convert to DTO
            StudentResponse response = convertToDto(updatedStudent);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }


    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable String id) {
        try {
            // Verify student exists
            studentService.findById(id);

            // Delete if exists
            studentService.deleteStudent(id);

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    // Helper method for consistency with other controllers
    private StudentResponse convertToDto(Student student) {
        StudentResponse dto = new StudentResponse();
        dto.setId(student.getId());
        dto.setName(student.getName());
        dto.setCourses(student.getCourses());
        dto.setPhoneNumber(student.getPhoneNumber());
        dto.setDateOfBirth(student.getDateOfBirth());

        return dto;
    }
}
