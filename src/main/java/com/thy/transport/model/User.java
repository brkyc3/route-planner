package com.thy.transport.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_username", columnList = "username")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    private String role;
} 