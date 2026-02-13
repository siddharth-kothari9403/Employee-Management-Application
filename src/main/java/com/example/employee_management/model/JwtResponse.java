package com.example.employee_management.model;

import java.io.Serial;
import java.io.Serializable;

public class JwtResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -8091879091924046844L;
    private final String JwtToken;

    public JwtResponse(String JwtToken) {
        this.JwtToken = JwtToken;
    }

    public String getToken() {
        return this.JwtToken;
    }
}