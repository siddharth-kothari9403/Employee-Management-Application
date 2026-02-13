package com.example.employee_management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "employee_details")
@Getter
@Setter
public class EmployeeDetails {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Integer id;

    @OneToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "age")
    private Integer age;

    @Column(name = "gender")
    private String gender;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNo;

    @Column(name = "department")
    private String department;
}
