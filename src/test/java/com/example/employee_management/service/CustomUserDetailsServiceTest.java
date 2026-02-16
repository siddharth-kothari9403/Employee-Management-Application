package com.example.employee_management.service;

import com.example.employee_management.entity.Role;
import com.example.employee_management.entity.User;
import com.example.employee_management.exceptions.UserNotFoundException;
import com.example.employee_management.model.UserDTO;
import com.example.employee_management.repository.RoleRepository;
import com.example.employee_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private User adminUser;
    private User hrUser;
    private User normalUser;
    private UserDTO normalDTO;
    private UserDTO hrDTO;
    private UserDTO adminDTO;
    private Role adminRole;
    private Role hrRole;
    private Role normalRole;

    @BeforeEach
    public void setUp() {

        this.adminRole = new Role("ADMIN");
        this.adminUser = new User(1, "admin", "test123", Set.of(this.adminRole));
        this.adminDTO = new UserDTO("admin", "test123");

        this.hrRole = new Role("HR_MANAGER");
        this.hrUser = new User(2, "hr", "test123", Set.of(this.hrRole));
        this.hrDTO = new UserDTO("hr",  "test123");

        this.normalRole = new Role("USER");
        this.normalUser = new User(3, "employee", "test123", Set.of(this.normalRole));
        this.normalDTO = new UserDTO("employee", "test123");
    }

    @Test
    public void getUserByIdShouldReturnUserForValidUser() {
        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.ofNullable(adminUser));
        User user = userDetailsService.getUserById(adminUser.getId());
        assertEquals(adminUser, user);
    }

    @Test
    public void getUserByIdShouldThrowExceptionForInvalidUser() {
        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userDetailsService.getUserById(adminUser.getId()));
    }

    @Test
    public void saveUserShouldReturnSavedUser(){
        when(userRepository.save(any())).thenReturn(normalUser);
        when(roleRepository.findRoleByName(any())).thenReturn(normalRole);
        User user = userDetailsService.saveUser(normalDTO);
        assertEquals(normalUser, user);
    }

    @Test
    public void saveHrShouldReturnSavedHr(){
        when(userRepository.save(any())).thenReturn(hrUser);
        when(roleRepository.findRoleByName(any())).thenReturn(hrRole);
        User user = userDetailsService.saveHR(hrDTO);
        assertEquals(hrUser, user);
    }

    @Test
    public void saveAdminShouldReturnSavedAdmin(){
        when(userRepository.save(any())).thenReturn(adminUser);
        when(roleRepository.findRoleByName(any())).thenReturn(adminRole);
        User user = userDetailsService.saveAdmin(adminDTO);
        assertEquals(adminUser, user);
    }
}
