package com.example.employee_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Time;

@Entity
@Table(name = "login_logout_times")
@Getter
@Setter
public class LoginLogoutTimes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer entry_id;

    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH })
    @JoinColumn(name = "employee_id")
    private EmployeeDetails employeeDetails;

    @Column(name = "date")
    private Date date;

    @Column(name = "time")
    private Time time;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", columnDefinition = "ENUM('login', 'logout')")
    private EntryType entryType;
}
