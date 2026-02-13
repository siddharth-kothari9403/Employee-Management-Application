package com.example.employee_management.security;

import com.example.employee_management.entity.EmployeeDetails;
import com.example.employee_management.entity.Role;
import com.example.employee_management.entity.User;
import com.example.employee_management.model.CustomUserDetails;
import org.jspecify.annotations.NullUnmarked;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomUserDetailsSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
    @Override
    @NullUnmarked
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Set<Role> roles = Arrays.stream(annotation.roles())
                .map(roleName -> {
                    Role role = new Role();
                    role.setName(roleName);
                    return role;
                }).collect(Collectors.toSet());

        EmployeeDetails employeeDetails = new EmployeeDetails();
        employeeDetails.setId(annotation.employeeId());

        User user = new User();
        user.setUsername(annotation.username());
        user.setPassword(annotation.password());
        user.setRoles(roles);
        user.setEmployeeDetails(employeeDetails);

        CustomUserDetails principal = new CustomUserDetails(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );

        context.setAuthentication(auth);
        return context;
    }
}

