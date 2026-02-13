package com.example.employee_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Role {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "role_id")
    private long id;

    public Role(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Role(){

    }

    @Column(name = "role_name")
    private String name;
}

