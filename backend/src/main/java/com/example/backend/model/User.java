package com.example.backend.model;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document(collection = "users")
@CompoundIndex(def = "{'username': 1}", unique = true)
public class User {
    @Id
    private String id;
    private String username;
    private String password;
    private String email;

}
