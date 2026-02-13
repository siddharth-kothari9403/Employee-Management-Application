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
public class UserDetailsServiceTest {

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

        this.adminRole = new Role();
        adminRole.setName("ADMIN");

        this.adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword("test123");
        adminUser.setId(1);
        adminUser.setRoles(Set.of(adminRole));

        this.adminDTO = new UserDTO();
        adminDTO.setUsername("admin");
        adminDTO.setPassword("test123");

        this.hrRole = new Role();
        hrRole.setName("HR_MANAGER");

        this.hrUser = new User();
        hrUser.setUsername("hr");
        hrUser.setPassword("test123");
        hrUser.setId(2);
        hrUser.setRoles(Set.of(hrRole));

        this.hrDTO = new UserDTO();
        this.hrDTO.setUsername("hr");
        this.hrDTO.setPassword("test123");

        this.normalRole = new Role();
        normalRole.setName("USER");

        this.normalUser = new User();
        normalUser.setUsername("employee");
        normalUser.setPassword("test123");
        normalUser.setId(3);
        normalUser.setRoles(Set.of(normalRole));

        this.normalDTO = new UserDTO();
        this.normalDTO.setUsername("employee");
        this.normalDTO.setPassword("test123");
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
