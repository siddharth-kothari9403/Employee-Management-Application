package com.example.employee_management.controller;

import com.example.employee_management.config.JwtAuthenticationEntryPoint;
import com.example.employee_management.config.JwtRequestFilter;
import com.example.employee_management.config.JwtTokenUtil;
import com.example.employee_management.config.WebSecurityConfig;
import com.example.employee_management.entity.Role;
import com.example.employee_management.entity.User;
import com.example.employee_management.model.CustomUserDetails;
import com.example.employee_management.model.UserDTO;
import com.example.employee_management.service.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@Import(WebSecurityConfig.class)
@AutoConfigureMockMvc
public class AuthControllerTest {

    private final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtTokenUtil jwtTokenUtil;

    @MockitoBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private JwtRequestFilter jwtRequestFilter = new JwtRequestFilter(customUserDetailsService, jwtTokenUtil);

    private User adminUser;
    private User hrUser;
    private User normalUser;
    private Role adminRole;
    private Role hrRole;
    private Role normalRole;
    private UserDTO adminUserDTO;
    private UserDTO hrUserDTO;
    private UserDTO normalUserDTO;

    @BeforeEach
    public void setUp(){

        this.adminRole = new Role("ADMIN");
        this.hrRole = new Role("HR_MANAGER");
        this.normalRole = new Role("USER");
        this.adminUser = new User(1, "admin", "test123", Set.of(adminRole));
        this.hrUser = new User(2, "hr", "test123", Set.of(hrRole));
        this.normalUser = new User(3, "employee", "test123", Set.of(normalRole));
        this.adminUserDTO = new UserDTO("admin", "test123");
        this.hrUserDTO = new UserDTO("hr", "test123");
        this.normalUserDTO = new UserDTO("employee", "test123");
    }

    @AfterEach
    public void tearDown(){
        adminRole = null;
        hrRole = null;
        normalRole = null;
        adminUser = null;
        hrUser = null;
        normalUser = null;
        adminUserDTO = null;
        hrUserDTO = null;
        normalUserDTO = null;
    }

    @Test
    @WithMockUser(username = "admin", password = "test123", roles = "ADMIN")
    public void addUserWithAdminRoleShouldPass() throws Exception {
        when(customUserDetailsService.saveUser(any())).thenReturn(normalUser);
        mockMvc.perform(post("/v1/register_user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(normalUserDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "hr", password = "test123", roles = "HR_MANAGER")
    public void addUserWithHRRoleShouldFail() throws Exception {
        when(customUserDetailsService.saveUser(any())).thenReturn(normalUser);
        mockMvc.perform(post("/v1/register_user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(normalUserDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "employee", password = "test123", roles = "USER")
    public void addUserWithUserRoleShouldFail() throws Exception {
        when(customUserDetailsService.saveUser(any())).thenReturn(normalUser);
        mockMvc.perform(post("/v1/register_user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(normalUserDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", password = "test123", roles = "ADMIN")
    public void addHRWithAdminRoleShouldPass() throws Exception {
        when(customUserDetailsService.saveUser(any())).thenReturn(hrUser);
        mockMvc.perform(post("/v1/register_hr_manager")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(hrUserDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "hr", password = "test123", roles = "HR_MANAGER")
    public void addHRWithHRRoleShouldFail() throws Exception {
        when(customUserDetailsService.saveUser(any())).thenReturn(hrUser);
        mockMvc.perform(post("/v1/register_hr_manager")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(hrUserDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "employee", password = "test123", roles = "USER")
    public void addHRWithUserRoleShouldFail() throws Exception {
        when(customUserDetailsService.saveUser(any())).thenReturn(hrUser);
        mockMvc.perform(post("/v1/register_hr_manager")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(hrUserDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", password = "test123", roles = "ADMIN")
    public void addAdminWithAdminRoleShouldPass() throws Exception {
        when(customUserDetailsService.saveUser(any())).thenReturn(adminUser);
        mockMvc.perform(post("/v1/register_admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(adminUserDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "hr", password = "test123", roles = "HR_MANAGER")
    public void addAdminWithHRRoleShouldFail() throws Exception {
        when(customUserDetailsService.saveUser(any())).thenReturn(adminUser);
        mockMvc.perform(post("/v1/register_admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(adminUserDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "employee", password = "test123", roles = "USER")
    public void addAdminWithUserRoleShouldFail() throws Exception {
        when(customUserDetailsService.saveUser(any())).thenReturn(adminUser);
        mockMvc.perform(post("/v1/register_admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(adminUserDTO)))
                .andExpect(status().isForbidden());
    }
}
