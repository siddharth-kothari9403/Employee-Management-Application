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

        this.adminRole = new Role("ADMIN");
        Role hrRole = new Role("HR_MANAGER");
        Role userRole = new Role("USER");

        entityManager.persist(adminRole);
        entityManager.persist(hrRole);
        entityManager.persist(userRole);

        u1 = new User("admin", "test123", Set.of(adminRole));
        User u2 = new User("hr", "test123", Set.of(hrRole));
        User u3 = new User("john", "test123", Set.of(userRole));
        User u4 = new User("alice", "test123", Set.of(userRole));
        User u5 = new User("bob", "test123", Set.of(userRole));

        entityManager.persist(u1);
        entityManager.persist(u2);
        entityManager.persist(u3);
        entityManager.persist(u4);
        entityManager.persist(u5);

        e1 = new EmployeeDetails("Admin", null, null, null, null, null, "Admin", u1);
        EmployeeDetails e2 = new EmployeeDetails("HR", null, null, null, null, null, "HR", u2);
        EmployeeDetails e3 = new EmployeeDetails("John", null, null, null, null, null, "Engineering", u3);
        EmployeeDetails e4 = new EmployeeDetails("Alice", null, null, null, null, null, "Marketing", u4);
        EmployeeDetails e5 = new EmployeeDetails("Bob", null, null, null, null, null, "Engineering", u5);

        entityManager.persist(e1);
        entityManager.persist(e2);
        entityManager.persist(e3);
        entityManager.persist(e4);
        entityManager.persist(e5);

        LoginLogoutTimes l1 = new LoginLogoutTimes(null, null, EntryType.login, e1);
        LoginLogoutTimes l2 = new LoginLogoutTimes(null, null, EntryType.login, e2);
        LoginLogoutTimes l3 = new LoginLogoutTimes(null, null, EntryType.login, e3);
        LoginLogoutTimes l4 = new LoginLogoutTimes(null, null, EntryType.login, e4);
        LoginLogoutTimes l5 = new LoginLogoutTimes(null, null, EntryType.logout, e1);

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
