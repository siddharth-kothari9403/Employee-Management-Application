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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
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

    @MockitoBean
    private JwtRequestFilter jwtRequestFilter;

    private User adminUser;
    private User hrUser;
    private User normalUser;
    private Role adminRole;
    private Role hrRole;
    private Role normalRole;
    private EmployeeDetails adminEmployeeDetails;
    private EmployeeDetails hrEmployeeDetails;
    private EmployeeDetails normalEmployeeDetails;
    private List<EmployeeDetails> employeeDetails;
    private List<EmployeeDetails> engineeringEmployeeDetails;

    @BeforeEach
    public void setup() throws Exception {

        doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            jakarta.servlet.FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(jwtRequestFilter).doFilter(any(), any(), any());

        this.employeeDetails = new ArrayList<>();
        this.engineeringEmployeeDetails = new ArrayList<>();

        this.adminRole = new Role();
        adminRole.setName("ADMIN");

        this.adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword("test123");
        adminUser.setId(1);
        adminUser.setRoles(Set.of(adminRole));

        this.adminEmployeeDetails = new EmployeeDetails();
        adminEmployeeDetails.setId(1);
        adminEmployeeDetails.setFirstName("test");
        adminEmployeeDetails.setLastName("test");
        adminEmployeeDetails.setDepartment("Admin");
        adminEmployeeDetails.setUser(adminUser);

        employeeDetails.add(adminEmployeeDetails);

        this.hrRole = new Role();
        hrRole.setName("HR_MANAGER");

        this.hrUser = new User();
        hrUser.setUsername("hr");
        hrUser.setPassword("test123");
        hrUser.setId(2);
        hrUser.setRoles(Set.of(hrRole));

        this.hrEmployeeDetails = new EmployeeDetails();
        hrEmployeeDetails.setId(2);
        hrEmployeeDetails.setFirstName("test");
        hrEmployeeDetails.setLastName("test");
        hrEmployeeDetails.setDepartment("HR");
        hrEmployeeDetails.setUser(hrUser);

        employeeDetails.add(hrEmployeeDetails);

        this.normalRole = new Role();
        normalRole.setName("USER");

        this.normalUser = new User();
        normalUser.setUsername("user");
        normalUser.setPassword("test123");
        normalUser.setId(3);
        normalUser.setRoles(Set.of(normalRole));

        this.normalEmployeeDetails = new EmployeeDetails();
        normalEmployeeDetails.setId(3);
        normalEmployeeDetails.setFirstName("test");
        normalEmployeeDetails.setLastName("test");
        normalEmployeeDetails.setDepartment("Engineering");
        normalEmployeeDetails.setUser(normalUser);

        employeeDetails.add(normalEmployeeDetails);
        engineeringEmployeeDetails.add(normalEmployeeDetails);
    }

    @AfterEach
    public void tearDown(){
        employeeDetails = null;
        engineeringEmployeeDetails = null;
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
        when(employeeDetailsService.findAll()).thenReturn(employeeDetails);
        mockMvc.perform(get("/v1/employee/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(3));
    }

    @Test
    @WithMockUser(username = "hr", password = "test123", roles = "HR_MANAGER")
    public void getAllEmployeesWithHRManagerRoleShouldPass() throws Exception {
        when(employeeDetailsService.findAll()).thenReturn(employeeDetails);
        mockMvc.perform(get("/v1/employee/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    @WithMockUser(username = "user", password = "test123", roles = "USER")
    public void getAllEmployeesWithUserRoleShouldFail() throws Exception {
        when(employeeDetailsService.findAll()).thenReturn(employeeDetails);
        mockMvc.perform(get("/v1/employee/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", password = "test123", roles = "ADMIN")
    public void getEmployeesByDeptWithAdminRoleShouldPass() throws Exception {
        when(employeeDetailsService.findByDepartment(any())).thenReturn(engineeringEmployeeDetails);
        mockMvc.perform(get("/v1/employee/departments?departmentName=Engineering")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(username = "hr", password = "test123", roles = "HR_MANAGER")
    public void getEmployeesByDeptWithHRManagerRoleShouldPass() throws Exception {
        when(employeeDetailsService.findByDepartment(any())).thenReturn(engineeringEmployeeDetails);
        mockMvc.perform(get("/v1/employee/departments?departmentName=Engineering")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(username = "user", password = "test123", roles = "USER")
    public void getEmployeesByDeptWithUserRoleShouldFail() throws Exception {
        when(employeeDetailsService.findAll()).thenReturn(engineeringEmployeeDetails);
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