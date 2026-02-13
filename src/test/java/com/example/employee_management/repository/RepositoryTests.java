package com.example.employee_management.repository;

import com.example.employee_management.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RepositoryTests {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private LoginLogoutTimesRepository loginLogoutTimesRepository;

    @Autowired
    private TestEntityManager entityManager;

    private EmployeeDetails e1;
    private Role adminRole;
    private User u1;

    @BeforeEach
    void setUp() {

        adminRole = new Role();
        adminRole.setName("ADMIN");

        Role hrRole = new Role();
        hrRole.setName("HR_MANAGER");

        Role userRole = new Role();
        userRole.setName("USER");

        entityManager.persist(adminRole);
        entityManager.persist(hrRole);
        entityManager.persist(userRole);

        u1 = new User();
        u1.setUsername("admin");
        u1.setPassword("test123");
        u1.setRoles(Set.of(adminRole));

        User u2 = new User();
        u2.setUsername("hr");
        u2.setPassword("test123");
        u2.setRoles(Set.of(hrRole));

        User u3 = new User();
        u3.setUsername("john");
        u3.setPassword("test123");
        u3.setRoles(Set.of(userRole));

        User u4 = new User();
        u4.setUsername("alice");
        u4.setPassword("test123");
        u4.setRoles(Set.of(userRole));

        User u5 = new User();
        u5.setUsername("bob");
        u5.setPassword("test123");
        u5.setRoles(Set.of(userRole));

        entityManager.persist(u1);
        entityManager.persist(u2);
        entityManager.persist(u3);
        entityManager.persist(u4);
        entityManager.persist(u5);

        e1 = new EmployeeDetails();
        e1.setFirstName("Admin");
        e1.setDepartment("Admin");

        EmployeeDetails e2 = new EmployeeDetails();
        e2.setFirstName("HR");
        e2.setDepartment("HR");

        EmployeeDetails e3 = new EmployeeDetails();
        e3.setFirstName("John");
        e3.setDepartment("Engineering");

        EmployeeDetails e4 = new EmployeeDetails();
        e4.setFirstName("Alice");
        e4.setDepartment("Marketing");

        EmployeeDetails e5 = new EmployeeDetails();
        e5.setFirstName("Bob");
        e5.setDepartment("Engineering");

        entityManager.persist(e1);
        entityManager.persist(e2);
        entityManager.persist(e3);
        entityManager.persist(e4);
        entityManager.persist(e5);

        LoginLogoutTimes l1 = new LoginLogoutTimes();
        l1.setEmployeeDetails(e1);
        l1.setEntryType(EntryType.login);

        LoginLogoutTimes l2 = new LoginLogoutTimes();
        l2.setEmployeeDetails(e2);
        l2.setEntryType(EntryType.login);

        LoginLogoutTimes l3 = new LoginLogoutTimes();
        l3.setEmployeeDetails(e3);
        l3.setEntryType(EntryType.login);

        LoginLogoutTimes l4 = new LoginLogoutTimes();
        l4.setEmployeeDetails(e4);
        l4.setEntryType(EntryType.login);

        LoginLogoutTimes l5 = new LoginLogoutTimes();
        l5.setEmployeeDetails(e1);
        l5.setEntryType(EntryType.logout);

        entityManager.persist(l1);
        entityManager.persist(l2);
        entityManager.persist(l3);
        entityManager.persist(l4);
        entityManager.persist(l5);

        entityManager.flush();
    }

    @Test
    void findEmployeeByDepartmentShouldReturnMatchingEmployees() {
        List<EmployeeDetails> result = employeeRepository.findByDepartment("Engineering");
        assertThat(result).hasSize(2);
        assertThat(result.getFirst().getFirstName()).isEqualTo("John");
    }

    @Test
    void findEmployeeByDepartmentShouldReturnEmptyListWhenNoMatch() {
        List<EmployeeDetails> result = employeeRepository.findByDepartment("Product");
        assertThat(result).isEmpty();
    }

    @Test
    void findTimesByDepartmentShouldReturnMatchingTimes() {
        List<LoginLogoutTimes> result = loginLogoutTimesRepository.findByDepartment("Engineering");
        System.out.println(result);
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getEntryType()).isEqualTo(EntryType.login);
    }

    @Test
    void findTimesByDepartmentShouldReturnEmptyListWhenNoMatch() {
        List<LoginLogoutTimes> result = loginLogoutTimesRepository.findByDepartment("Product");
        assertThat(result).isEmpty();
    }

    @Test
    void findTimesByEmployeeShouldReturnMatchingTimes() {
        List<LoginLogoutTimes> result = loginLogoutTimesRepository.getRecordsByEmployeeId(e1.getId());
        assertThat(result).hasSize(2);
        assertThat(result.getFirst().getEntryType()).isEqualTo(EntryType.login);
        assertThat(result.getLast().getEntryType()).isEqualTo(EntryType.logout);
        assertThat(result.getFirst().getEmployeeDetails().getFirstName()).isEqualTo("Admin");
    }

    @Test
    void findValidRolesShouldReturnMatchingRoles() {
        Role role = roleRepository.findRoleByName("ADMIN");
        assertThat(role.getName()).isEqualTo(adminRole.getName());
        assertThat(role.getId()).isEqualTo(adminRole.getId());
    }

    @Test
    void findInvalidRolesShouldReturnNull() {
        Role role = roleRepository.findRoleByName("NON-EXISTENT-ROLE");
        assertThat(role).isNull();
    }

    @Test
    void findValidUsersShouldReturnMatchingUsers() {
        User user = userRepository.findByUsername("admin");
        assertThat(user).isNotNull();
        assertThat(user.getRoles()).hasSize(1);
        assertThat(user.getUsername()).isEqualTo(u1.getUsername());
        assertThat(user.getPassword()).isEqualTo(u1.getPassword());
        assertThat(user.getId()).isEqualTo(u1.getId());
    }

    @Test
    void findInvalidUsersShouldReturnNull() {
        User user = userRepository.findByUsername("non-existent-user");
        assertThat(user).isNull();
    }
}
