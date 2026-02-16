package com.example.employee_management.controller;

import com.example.employee_management.config.JwtAuthenticationEntryPoint;
import com.example.employee_management.config.JwtRequestFilter;
import com.example.employee_management.config.JwtTokenUtil;
import com.example.employee_management.config.WebSecurityConfig;
import com.example.employee_management.entity.EmployeeDetails;
import com.example.employee_management.entity.Role;
import com.example.employee_management.entity.User;
import com.example.employee_management.security.WithMockCustomUser;
import com.example.employee_management.service.EmployeeDetailsService;
import com.example.employee_management.service.CustomUserDetailsService;
import com.example.employee_management.service.SecurityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeRestController.class)
@Import(WebSecurityConfig.class)
@AutoConfigureMockMvc
public class EmployeeRestControllerTest {

    private final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeDetailsService employeeDetailsService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean(name = "securityService")
    private SecurityService securityService;

    @MockitoBean
    private JwtTokenUtil jwtTokenUtil;

    @MockitoBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private final JwtRequestFilter jwtRequestFilter = new JwtRequestFilter(customUserDetailsService, jwtTokenUtil);

    private User adminUser;
    private User hrUser;
    private User normalUser;
    private Role adminRole;
    private Role hrRole;
    private Role normalRole;
    private EmployeeDetails adminEmployeeDetails;
    private EmployeeDetails hrEmployeeDetails;
    private EmployeeDetails normalEmployeeDetails;
    private List<EmployeeDetails> employeeDetailsList;
    private List<EmployeeDetails> engineeringEmployeeDetailsList;

    @BeforeEach
    public void setup() {

        this.adminRole = new Role("ADMIN");
        this.adminUser = new User(1, "admin", "test123", Set.of(adminRole));
        this.adminEmployeeDetails = new EmployeeDetails(1, "test", "test", null, null, null, null, "Admin", adminUser);

        this.hrRole = new Role("HR_MANAGER");
        this.hrUser = new User(2, "hr", "test123", Set.of(hrRole));
        this.hrEmployeeDetails = new EmployeeDetails(2, "test", "test", null, null, null, null, "HR", hrUser);

        this.normalRole = new Role("USER");
        this.normalUser = new User(3, "user", "test123", Set.of(normalRole));
        this.normalEmployeeDetails = new EmployeeDetails(3, "test", "test", null, null, null, null, "Engineering", normalUser);

        this.employeeDetailsList = new ArrayList<>(List.of(adminEmployeeDetails, hrEmployeeDetails, normalEmployeeDetails));
        this.engineeringEmployeeDetailsList = new ArrayList<>(List.of(normalEmployeeDetails));
    }

    @AfterEach
    public void tearDown(){
        employeeDetailsList = null;
        engineeringEmployeeDetailsList = null;
        adminUser = null;
        hrUser = null;
        normalUser = null;
        adminEmployeeDetails = null;
        hrEmployeeDetails = null;
        normalEmployeeDetails = null;
        adminRole = null;
        hrRole = null;
        normalRole = null;
    }

    @Test
    @WithMockUser(username = "admin", password = "test123", roles = "ADMIN")
    public void getAllEmployeesWithAdminRoleShouldPass() throws Exception {
        when(employeeDetailsService.findAll()).thenReturn(employeeDetailsList);
        mockMvc.perform(get("/v1/employee/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(3));
    }

    @Test
    @WithMockUser(username = "hr", password = "test123", roles = "HR_MANAGER")
    public void getAllEmployeesWithHRManagerRoleShouldPass() throws Exception {
        when(employeeDetailsService.findAll()).thenReturn(employeeDetailsList);
        mockMvc.perform(get("/v1/employee/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    @WithMockUser(username = "user", password = "test123", roles = "USER")
    public void getAllEmployeesWithUserRoleShouldFail() throws Exception {
        when(employeeDetailsService.findAll()).thenReturn(employeeDetailsList);
        mockMvc.perform(get("/v1/employee/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", password = "test123", roles = "ADMIN")
    public void getEmployeesByDeptWithAdminRoleShouldPass() throws Exception {
        when(employeeDetailsService.findByDepartment(any())).thenReturn(engineeringEmployeeDetailsList);
        mockMvc.perform(get("/v1/employee/departments?departmentName=Engineering")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(username = "hr", password = "test123", roles = "HR_MANAGER")
    public void getEmployeesByDeptWithHRManagerRoleShouldPass() throws Exception {
        when(employeeDetailsService.findByDepartment(any())).thenReturn(engineeringEmployeeDetailsList);
        mockMvc.perform(get("/v1/employee/departments?departmentName=Engineering")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(username = "user", password = "test123", roles = "USER")
    public void getEmployeesByDeptWithUserRoleShouldFail() throws Exception {
        when(employeeDetailsService.findAll()).thenReturn(engineeringEmployeeDetailsList);
        mockMvc.perform(get("/v1/employee/departments?departmentName=Engineering")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(username = "admin", password = "test123", roles = "ADMIN", employeeId = 1)
    public void getEmployeeWithAdminRoleShouldPass() throws Exception {
        when(employeeDetailsService.findById(any())).thenReturn(adminEmployeeDetails);
        when(securityService.isEmployeeIdCorrect(eq(1), any())).thenReturn(true);
        mockMvc.perform(get("/v1/employee/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser(username = "hr", password = "test123", roles = "HR_MANAGER", employeeId = 2)
    public void getEmployeeWithHRManagerRoleShouldPass() throws Exception {
        when(employeeDetailsService.findById(any())).thenReturn(adminEmployeeDetails);
        when(securityService.isEmployeeIdCorrect(eq(1), any())).thenReturn(false);
        mockMvc.perform(get("/v1/employee/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser(username = "user", password = "test123", roles = "USER", employeeId = 3)
    public void getEmployeeWithUserRoleShouldFailForOtherUser() throws Exception {
        when(employeeDetailsService.findById(any())).thenReturn(adminEmployeeDetails);
        when(securityService.isEmployeeIdCorrect(eq(1), any())).thenReturn(false);
        mockMvc.perform(get("/v1/employee/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(username = "user", password = "test123", roles = "USER", employeeId = 3)
    public void getEmployeeWithUserRoleShouldPassForSelf() throws Exception {
        when(employeeDetailsService.findById(any())).thenReturn(normalEmployeeDetails);
        when(securityService.isEmployeeIdCorrect(eq(3), any())).thenReturn(true);
        mockMvc.perform(get("/v1/employee/3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", password = "test123", roles = "ADMIN")
    public void addEmployeeWithAdminRoleShouldPass() throws Exception {
        when(employeeDetailsService.addEmployee(any())).thenReturn(adminEmployeeDetails);
        when(customUserDetailsService.getUserById(1)).thenReturn(adminUser);
        mockMvc.perform(post("/v1/employee/add/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(adminEmployeeDetails)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "hr", password = "test123", roles = "HR_MANAGER")
    public void addEmployeeWithHRRoleShouldFail() throws Exception {
        when(employeeDetailsService.addEmployee(any())).thenReturn(hrEmployeeDetails);
        when(customUserDetailsService.getUserById(2)).thenReturn(hrUser);
        mockMvc.perform(post("/v1/employee/add/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(hrEmployeeDetails)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", password = "test123", roles = "USER")
    public void addEmployeeWithUserRoleShouldFail() throws Exception {
        when(employeeDetailsService.addEmployee(any())).thenReturn(normalEmployeeDetails);
        when(customUserDetailsService.getUserById(3)).thenReturn(normalUser);
        mockMvc.perform(post("/v1/employee/add/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(normalEmployeeDetails)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", password = "test123", roles = "ADMIN")
    public void updateEmployeeWithAdminRoleShouldPass() throws Exception {
        when(employeeDetailsService.findById(any())).thenReturn(adminEmployeeDetails);
        when(employeeDetailsService.updateEmployee(any())).thenReturn(adminEmployeeDetails);
        mockMvc.perform(put("/v1/employee/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(adminEmployeeDetails)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "hr", password = "test123", roles = "HR_MANAGER")
    public void updateEmployeeWithHRRoleShouldFail() throws Exception {
        when(employeeDetailsService.findById(any())).thenReturn(hrEmployeeDetails);
        when(employeeDetailsService.updateEmployee(any())).thenReturn(hrEmployeeDetails);
        mockMvc.perform(put("/v1/employee/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(hrEmployeeDetails)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", password = "test123", roles = "USER")
    public void updateEmployeeWithUserRoleShouldFail() throws Exception {
        when(employeeDetailsService.findById(any())).thenReturn(normalEmployeeDetails);
        when(employeeDetailsService.updateEmployee(any())).thenReturn(normalEmployeeDetails);
        mockMvc.perform(put("/v1/employee/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(hrEmployeeDetails)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", password = "test123", roles = "ADMIN")
    public void deleteEmployeeWithAdminRoleShouldPass() throws Exception {
        when(employeeDetailsService.findById(any())).thenReturn(adminEmployeeDetails);
        when(employeeDetailsService.updateEmployee(any())).thenReturn(adminEmployeeDetails);
        mockMvc.perform(delete("/v1/employee/delete/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "hr", password = "test123", roles = "HR_MANAGER")
    public void deleteEmployeeWithHRRoleShouldFail() throws Exception {
        when(employeeDetailsService.findById(any())).thenReturn(hrEmployeeDetails);
        when(employeeDetailsService.updateEmployee(any())).thenReturn(hrEmployeeDetails);
        mockMvc.perform(delete("/v1/employee/delete/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", password = "test123", roles = "USER")
    public void deleteEmployeeWithUserRoleShouldFail() throws Exception {
        when(employeeDetailsService.findById(any())).thenReturn(normalEmployeeDetails);
        when(employeeDetailsService.updateEmployee(any())).thenReturn(normalEmployeeDetails);
        mockMvc.perform(delete("/v1/employee/delete/3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}