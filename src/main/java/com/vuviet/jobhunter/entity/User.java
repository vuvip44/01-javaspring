package com.vuviet.jobhunter.entity;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name="users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long id;
    private String name;
    private String email;
    private String password;
}
