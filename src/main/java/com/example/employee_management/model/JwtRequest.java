package com.example.employee_management.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class JwtRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 5926468583005150707L;

    private String username;
    private String password;

    //need default constructor for JSON Parsing
    public JwtRequest() {}

    public JwtRequest(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }
}

