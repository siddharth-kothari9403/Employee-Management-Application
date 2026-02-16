package com.example.employee_management.component;

import com.example.employee_management.config.JwtTokenUtil;
import com.example.employee_management.entity.EmployeeDetails;
import com.example.employee_management.entity.Role;
import com.example.employee_management.entity.User;
import com.example.employee_management.model.CustomUserDetails;
import com.example.employee_management.model.UserDTO;
import com.example.employee_management.repository.EmployeeRepository;
import com.example.employee_management.repository.UserRepository;
import com.example.employee_management.service.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class EmployeeAdditionComponentTest {

    private final ObjectMapper om = new ObjectMapper();
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    private Role adminRole;
    private Role hrRole;
    private Role employeeRole;
    private User u1;
    private User u2;
    private EmployeeDetails e1;
    private EmployeeDetails e2;

    @BeforeEach
    public void setup() {
        adminRole = new Role();
        adminRole.setName("ADMIN");

        hrRole = new Role();
        hrRole.setName("HR_MANAGER");

        employeeRole = new Role();
        employeeRole.setName("USER");

        entityManager.persist(adminRole);
        entityManager.persist(hrRole);
        entityManager.persist(employeeRole);

        u1 = new User();
        u1.setUsername("admin");
        u1.setPassword(passwordEncoder.encode("test123"));
        u1.setRoles(Set.of(adminRole));

        u2 = new User();
        u2.setUsername("hr");
        u2.setPassword(passwordEncoder.encode("test123"));
        u2.setRoles(Set.of(hrRole));

        entityManager.persist(u2);

        e1 = new EmployeeDetails();
        e1.setFirstName("Admin");
        e1.setDepartment("Admin");

        e2 = new EmployeeDetails();
        e2.setFirstName("hr");
        e2.setDepartment("HR");

        entityManager.persist(e1);
        entityManager.persist(e2);
    }

    @AfterEach
    public void teardown() {
        entityManager.clear();
        this.adminRole = null;
        this.hrRole = null;
        this.employeeRole = null;
        this.u1 = null;
        this.u2 = null;
        this.e1 = null;
        this.e2 = null;
    }

    @Test
    public void adminCreatingEmployeeShouldBeSuccessful() throws Exception {

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("admin");
        userDTO.setPassword("test123");

        MvcResult mvcAuthenticateResult = mockMvc.perform(post("/v1/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String authenticateResponse = mvcAuthenticateResult.getResponse().getContentAsString();
        String token = om.readTree(authenticateResponse).get("token").asText();

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("admin");
        assertTrue(jwtTokenUtil.validateToken(token, userDetails));

        UserDTO userToAdd = new UserDTO();
        userToAdd.setUsername("employee");
        userToAdd.setPassword(passwordEncoder.encode("test123"));

        mockMvc.perform(post("/v1/register_user")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(userToAdd))
                        ).andExpect(status().isOk());

        User user = userRepository.findByUsername("employee");
        assertThat(user).isNotNull();
        assertThat(user.getRoles()).hasSize(1);
        assertThat(user.getRoles()).contains(employeeRole);
        assertThat(user.getUsername()).isEqualTo("employee");

        EmployeeDetails employeeDetails = new EmployeeDetails();
        employeeDetails.setFirstName("employee");
        employeeDetails.setDepartment("Engineering");
        employeeDetails.setLastName("employee");

        MvcResult addEmployeeResult = mockMvc.perform(post("/v1/employee/add/" + user.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(employeeDetails)))
                .andExpect(status().isOk())
                .andReturn();

        String employeeResponse = addEmployeeResult.getResponse().getContentAsString();
        int empId = om.readTree(employeeResponse).get("id").asInt();

        Optional<EmployeeDetails> optionalEmployeeDetails = employeeRepository.findById(empId);
        assertTrue(optionalEmployeeDetails.isPresent());
        assertThat(optionalEmployeeDetails.get().getLastName()).isEqualTo(employeeDetails.getLastName());
        assertThat(optionalEmployeeDetails.get().getDepartment()).isEqualTo(employeeDetails.getDepartment());
        assertThat(optionalEmployeeDetails.get().getFirstName()).isEqualTo(employeeDetails.getFirstName());

        MvcResult employeeDetailsGetResult = mockMvc.perform(get("/v1/employee/" + empId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String employeeDetailsResponse = employeeDetailsGetResult.getResponse().getContentAsString();
        EmployeeDetails employeeDetailsFromResponse = om.readValue(employeeDetailsResponse, EmployeeDetails.class);

        assertEquals(employeeDetailsFromResponse.getFirstName(), employeeDetails.getFirstName());
        assertEquals(employeeDetailsFromResponse.getLastName(), employeeDetails.getLastName());
        assertEquals(employeeDetailsFromResponse.getDepartment(), employeeDetails.getDepartment());
    }

    @Test
    public void adminCreatingHRShouldBeSuccessful() throws Exception {

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("admin");
        userDTO.setPassword("test123");

        MvcResult mvcAuthenticateResult = mockMvc.perform(post("/v1/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String authenticateResponse = mvcAuthenticateResult.getResponse().getContentAsString();
        String token = om.readTree(authenticateResponse).get("token").asText();

        UserDTO userToAdd = new UserDTO();
        userToAdd.setUsername("hr");
        userToAdd.setPassword(passwordEncoder.encode("test123"));

        mockMvc.perform(post("/v1/register_hr_manager")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userToAdd))
        ).andExpect(status().isOk());

        User user = userRepository.findByUsername("hr");
        assertThat(user).isNotNull();
        assertThat(user.getRoles()).hasSize(1);
        assertThat(user.getRoles()).contains(hrRole);
        assertThat(user.getUsername()).isEqualTo("hr");

        EmployeeDetails employeeDetails = new EmployeeDetails();
        employeeDetails.setFirstName("hr_first");
        employeeDetails.setDepartment("HR");
        employeeDetails.setLastName("hr_last");

        MvcResult addEmployeeResult = mockMvc.perform(post("/v1/employee/add/" + user.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(employeeDetails)))
                .andExpect(status().isOk())
                .andReturn();

        String employeeResponse = addEmployeeResult.getResponse().getContentAsString();
        int empId = om.readTree(employeeResponse).get("id").asInt();

        Optional<EmployeeDetails> optionalEmployeeDetails = employeeRepository.findById(empId);
        assertTrue(optionalEmployeeDetails.isPresent());
        assertThat(optionalEmployeeDetails.get().getLastName()).isEqualTo(employeeDetails.getLastName());
        assertThat(optionalEmployeeDetails.get().getDepartment()).isEqualTo(employeeDetails.getDepartment());
        assertThat(optionalEmployeeDetails.get().getFirstName()).isEqualTo(employeeDetails.getFirstName());

        MvcResult employeeDetailsGetResult = mockMvc.perform(get("/v1/employee/" + empId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String employeeDetailsResponse = employeeDetailsGetResult.getResponse().getContentAsString();
        EmployeeDetails employeeDetailsFromResponse = om.readValue(employeeDetailsResponse, EmployeeDetails.class);

        assertEquals(employeeDetailsFromResponse.getFirstName(), employeeDetails.getFirstName());
        assertEquals(employeeDetailsFromResponse.getLastName(), employeeDetails.getLastName());
        assertEquals(employeeDetailsFromResponse.getDepartment(), employeeDetails.getDepartment());
    }

    @Test
    public void nonAdminCreatingEmployeeShouldAuthenticateButFailAddition() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("hr");
        userDTO.setPassword("test123");

        MvcResult mvcAuthenticateResult = mockMvc.perform(post("/v1/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String authenticateResponse = mvcAuthenticateResult.getResponse().getContentAsString();
        String token = om.readTree(authenticateResponse).get("token").asText();

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("hr");
        assertTrue(jwtTokenUtil.validateToken(token, userDetails));

        UserDTO userToAdd = new UserDTO();
        userToAdd.setUsername("employee");
        userToAdd.setPassword(passwordEncoder.encode("test123"));

        mockMvc.perform(post("/v1/register_user")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userToAdd))
        ).andExpect(status().isForbidden());
    }

    @Test
    public void nonAdminCreatingHRShouldAuthenticateButFailAddition() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("hr");
        userDTO.setPassword("test123");

        MvcResult mvcAuthenticateResult = mockMvc.perform(post("/v1/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String authenticateResponse = mvcAuthenticateResult.getResponse().getContentAsString();
        String token = om.readTree(authenticateResponse).get("token").asText();

        UserDTO userToAdd = new UserDTO();
        userToAdd.setUsername("hr1");
        userToAdd.setPassword(passwordEncoder.encode("test123"));

        mockMvc.perform(post("/v1/register_hr_manager")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userToAdd))
        ).andExpect(status().isForbidden());
    }

    @Test
    public void invalidUserShouldFailAuthentication() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("invalid_user");
        userDTO.setPassword("test123");

        mockMvc.perform(post("/v1/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(userDTO)))
                .andExpect(status().isUnauthorized());
    }
}
