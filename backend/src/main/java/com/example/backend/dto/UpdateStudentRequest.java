package com.example.backend.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateStudentRequest {
    private String name;
    private List<String> courses;
    private String phoneNumber;
    private LocalDate dateOfBirth;
}
