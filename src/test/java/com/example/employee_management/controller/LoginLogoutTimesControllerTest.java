package com.example.employee_management.controller;

import com.example.employee_management.config.JwtAuthenticationEntryPoint;
import com.example.employee_management.config.JwtRequestFilter;
import com.example.employee_management.config.JwtTokenUtil;
import com.example.employee_management.config.WebSecurityConfig;
import com.example.employee_management.entity.*;
import com.example.employee_management.security.WithMockCustomUser;
import com.example.employee_management.service.EmployeeDetailsService;
import com.example.employee_management.service.CustomUserDetailsService;
import com.example.employee_management.service.LoginLogoutTimesService;
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

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginLogoutTimesController.class)
@Import(WebSecurityConfig.class)
@AutoConfigureMockMvc
public class LoginLogoutTimesControllerTest {

    private final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeDetailsService employeeDetailsService;

    @MockitoBean
    private LoginLogoutTimesService loginLogoutTimesService;

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

    private LoginLogoutTimes loginLogoutTimes1;
    private LoginLogoutTimes loginLogoutTimes2;
    private LoginLogoutTimes loginLogoutTimes3;
    private LoginLogoutTimes loginLogoutTimes4;
    private LoginLogoutTimes loginLogoutTimes5;
    private List<LoginLogoutTimes> loginLogoutTimesList;
    private List<LoginLogoutTimes> engineeringLoginLogoutTimesList;
    private List<LoginLogoutTimes> employeeLoginLogoutTimesList;
    private EmployeeDetails employeeDetails1;
    private EmployeeDetails employeeDetails2;
    private EmployeeDetails employeeDetails3;

    @BeforeEach
    public void setup() throws Exception {

        doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            jakarta.servlet.FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(jwtRequestFilter).doFilter(any(), any(), any());

        this.employeeDetails1 = new EmployeeDetails();
        this.employeeDetails1.setId(1);
        this.employeeDetails1.setDepartment("Engineering");

        this.employeeDetails2 = new EmployeeDetails();
        this.employeeDetails2.setId(2);
        this.employeeDetails2.setDepartment("HR");

        this.employeeDetails3 = new EmployeeDetails();
        this.employeeDetails3.setId(3);
        this.employeeDetails3.setDepartment("Engineering");

        this.loginLogoutTimesList = new ArrayList<>();
        this.employeeLoginLogoutTimesList = new ArrayList<>();
        this.engineeringLoginLogoutTimesList = new ArrayList<>();

        this.loginLogoutTimes1 = new LoginLogoutTimes();
        loginLogoutTimes1.setEntryType(EntryType.login);
        loginLogoutTimes1.setTime(Time.valueOf("08:45:00"));
        loginLogoutTimes1.setDate(Date.valueOf("2024-05-09"));
        loginLogoutTimes1.setEmployeeDetails(employeeDetails1);
        loginLogoutTimes1.setEntry_id(1);

        loginLogoutTimesList.add(loginLogoutTimes1);
        employeeLoginLogoutTimesList.add(loginLogoutTimes1);
        engineeringLoginLogoutTimesList.add(loginLogoutTimes1);

        this.loginLogoutTimes2 = new LoginLogoutTimes();
        loginLogoutTimes2.setEntryType(EntryType.logout);
        loginLogoutTimes2.setTime(Time.valueOf("09:15:00"));
        loginLogoutTimes2.setDate(Date.valueOf("2024-05-09"));
        loginLogoutTimes2.setEmployeeDetails(employeeDetails1);
        loginLogoutTimes2.setEntry_id(2);

        loginLogoutTimesList.add(loginLogoutTimes2);
        employeeLoginLogoutTimesList.add(loginLogoutTimes2);
        engineeringLoginLogoutTimesList.add(loginLogoutTimes2);

        this.loginLogoutTimes3 = new LoginLogoutTimes();
        loginLogoutTimes3.setEntryType(EntryType.login);
        loginLogoutTimes3.setTime(Time.valueOf("09:15:00"));
        loginLogoutTimes3.setDate(Date.valueOf("2024-05-09"));
        loginLogoutTimes3.setEmployeeDetails(employeeDetails2);
        loginLogoutTimes3.setEntry_id(3);

        loginLogoutTimesList.add(loginLogoutTimes3);

        this.loginLogoutTimes4 = new LoginLogoutTimes();
        loginLogoutTimes4.setEntryType(EntryType.login);
        loginLogoutTimes4.setTime(Time.valueOf("08:30:00"));
        loginLogoutTimes4.setDate(Date.valueOf("2024-05-09"));
        loginLogoutTimes4.setEmployeeDetails(employeeDetails3);
        loginLogoutTimes4.setEntry_id(4);

        loginLogoutTimesList.add(loginLogoutTimes4);
        engineeringLoginLogoutTimesList.add(loginLogoutTimes4);

        this.loginLogoutTimes5 = new LoginLogoutTimes();
        loginLogoutTimes5.setEntryType(EntryType.logout);
        loginLogoutTimes5.setTime(Time.valueOf("17:45:00"));
        loginLogoutTimes5.setDate(Date.valueOf("2024-05-09"));
        loginLogoutTimes5.setEmployeeDetails(employeeDetails3);
        loginLogoutTimes5.setEntry_id(5);
    }

    @AfterEach
    public void tearDown(){
        loginLogoutTimesList = null;
        engineeringLoginLogoutTimesList = null;
        loginLogoutTimes1 = null;
        loginLogoutTimes2 = null;
        loginLogoutTimes3 = null;
        loginLogoutTimes4 = null;
        employeeDetails1 = null;
        employeeDetails2 = null;
        employeeDetails3 = null;
    }

    @Test
    @WithMockUser(username = "admin", password = "test123", roles = "ADMIN")
    public void getAllEmployeesWithAdminRoleShouldPass() throws Exception {
        when(loginLogoutTimesService.findAll()).thenReturn(loginLogoutTimesList);
        mockMvc.perform(get("/v1/times/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(4));
    }

    @Test
    @WithMockUser(username = "hr", password = "test123", roles = "HR_MANAGER")
    public void getAllEmployeesWithHRManagerRoleShouldPass() throws Exception {
        when(loginLogoutTimesService.findAll()).thenReturn(loginLogoutTimesList);
        mockMvc.perform(get("/v1/times/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));
    }

    @Test
    @WithMockUser(username = "user", password = "test123", roles = "USER")
    public void getAllTimesWithUserRoleShouldFail() throws Exception {
        when(loginLogoutTimesService.findAll()).thenReturn(loginLogoutTimesList);
        mockMvc.perform(get("/v1/times/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(username = "admin", password = "test123", roles = "ADMIN", employeeId = 1)
    public void getTimesWithAdminRoleShouldPass() throws Exception {
        when(loginLogoutTimesService.findById(any())).thenReturn(loginLogoutTimes1);
        when(securityService.isOwner(eq(1), any())).thenReturn(true);
        mockMvc.perform(get("/v1/times/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser(username = "hr", password = "test123", roles = "HR_MANAGER", employeeId = 2)
    public void getTimesWithHRManagerRoleShouldPass() throws Exception {
        when(loginLogoutTimesService.findById(any())).thenReturn(loginLogoutTimes2);
        when(securityService.isOwner(eq(1), any())).thenReturn(false);
        mockMvc.perform(get("/v1/times/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser(username = "user", password = "test123", roles = "USER", employeeId = 3)
    public void getTimesWithUserRoleShouldFailForOtherUser() throws Exception {
        when(loginLogoutTimesService.findById(any())).thenReturn(loginLogoutTimes3);
        when(securityService.isOwner(eq(1), any())).thenReturn(false);
        mockMvc.perform(get("/v1/times/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(username = "user", password = "test123", roles = "USER", employeeId = 3)
    public void getTimesWithUserRoleShouldPassForSelf() throws Exception {
        when(loginLogoutTimesService.findById(any())).thenReturn(loginLogoutTimes4);
        when(securityService.isOwner(eq(4), any())).thenReturn(true);
        mockMvc.perform(get("/v1/times/4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser(username = "hr", password = "test123", roles = "HR_MANAGER", employeeId = 2)
    public void getTimesWithEmpIdWithAdminRoleShouldPass() throws Exception {
        when(loginLogoutTimesService.findByEmployeeId(any())).thenReturn(employeeLoginLogoutTimesList);
        when(securityService.isEmployeeIdCorrect(eq(1), any())).thenReturn(false);
        mockMvc.perform(get("/v1/times/employee/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser(username = "hr", password = "test123", roles = "HR_MANAGER", employeeId = 2)
    public void getTimesByEmpIdWithHRManagerRoleShouldPass() throws Exception {
        when(loginLogoutTimesService.findByEmployeeId(any())).thenReturn(employeeLoginLogoutTimesList);
        when(securityService.isEmployeeIdCorrect(eq(1), any())).thenReturn(false);
        mockMvc.perform(get("/v1/times/employee/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser(username = "user", password = "test123", roles = "USER", employeeId = 3)
    public void getEmployeeWithUserRoleShouldFailForOtherUser() throws Exception {
        when(loginLogoutTimesService.findByEmployeeId(any())).thenReturn(employeeLoginLogoutTimesList);
        when(securityService.isEmployeeIdCorrect(eq(1), any())).thenReturn(false);
        mockMvc.perform(get("/v1/times/employee/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(username = "user", password = "test123", roles = "USER", employeeId = 3)
    public void getEmployeeWithUserRoleShouldPassForSelf() throws Exception {
        when(loginLogoutTimesService.findByEmployeeId(any())).thenReturn(List.of(loginLogoutTimes4));
        when(securityService.isEmployeeIdCorrect(eq(3), any())).thenReturn(true);
        mockMvc.perform(get("/v1/times/employee/3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockCustomUser(username = "user", password = "test123", roles = "USER", employeeId = 3)
    public void loginWithCorrectIdShouldPass() throws Exception {
        when(employeeDetailsService.findById(any())).thenReturn(employeeDetails3);
        when(loginLogoutTimesService.save(any())).thenReturn(loginLogoutTimes4);
        when(securityService.isEmployeeIdCorrect(eq(3), any())).thenReturn(true);
        mockMvc.perform(post("/v1/times/login/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(loginLogoutTimes4)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.entryType").value(EntryType.login.toString()));
    }

    @Test
    @WithMockCustomUser(username = "user", password = "test123", roles = "USER", employeeId = 3)
    public void logoutWithCorrectIdShouldPass() throws Exception {
        when(employeeDetailsService.findById(any())).thenReturn(employeeDetails3);
        when(loginLogoutTimesService.save(any())).thenReturn(loginLogoutTimes5);
        when(securityService.isEmployeeIdCorrect(eq(3), any())).thenReturn(true);
        mockMvc.perform(post("/v1/times/logout/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(loginLogoutTimes5)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.entryType").value(EntryType.logout.toString()));
    }

    @Test
    @WithMockCustomUser(username = "user", password = "test123", roles = "USER", employeeId = 4)
    public void loginWithIncorrectIdShouldFail() throws Exception {
        when(employeeDetailsService.findById(any())).thenReturn(employeeDetails3);
        when(loginLogoutTimesService.save(any())).thenReturn(loginLogoutTimes4);
        when(securityService.isEmployeeIdCorrect(eq(3), any())).thenReturn(false);
        mockMvc.perform(post("/v1/times/login/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(loginLogoutTimes4)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockCustomUser(username = "user", password = "test123", roles = "USER", employeeId = 4)
    public void logoutWithIncorrectIdShouldFail() throws Exception {
        when(employeeDetailsService.findById(any())).thenReturn(employeeDetails3);
        when(loginLogoutTimesService.save(any())).thenReturn(loginLogoutTimes5);
        when(securityService.isEmployeeIdCorrect(eq(3), any())).thenReturn(false);
        mockMvc.perform(post("/v1/times/logout/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(loginLogoutTimes5)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", password = "test123", roles = "ADMIN")
    public void getEmployeesByDeptWithAdminRoleShouldPass() throws Exception {
        when(loginLogoutTimesService.findByDepartment(any())).thenReturn(engineeringLoginLogoutTimesList);
        mockMvc.perform(get("/v1/times/departments?departmentName=Engineering")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(3));
    }

    @Test
    @WithMockUser(username = "hr", password = "test123", roles = "HR_MANAGER")
    public void getEmployeesByDeptWithHRManagerRoleShouldPass() throws Exception {
        when(loginLogoutTimesService.findByDepartment(any())).thenReturn(engineeringLoginLogoutTimesList);
        mockMvc.perform(get("/v1/times/departments?departmentName=Engineering")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    @WithMockUser(username = "user", password = "test123", roles = "USER")
    public void getEmployeesByDeptWithUserRoleShouldFail() throws Exception {
        when(loginLogoutTimesService.findAll()).thenReturn(engineeringLoginLogoutTimesList);
        mockMvc.perform(get("/v1/times/departments?departmentName=Engineering")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
