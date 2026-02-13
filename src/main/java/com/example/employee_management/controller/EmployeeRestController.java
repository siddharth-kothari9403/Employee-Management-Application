package com.example.employee_management.controller;

import com.example.employee_management.entity.EmployeeDetails;
import com.example.employee_management.entity.User;
import com.example.employee_management.exceptions.EmployeeNotFoundException;
import com.example.employee_management.exceptions.UserNotFoundException;
import com.example.employee_management.service.EmployeeDetailsService;
import com.example.employee_management.service.CustomUserDetailsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/employee")
public class EmployeeRestController {

    private final EmployeeDetailsService employeeDetailsService;
    private final CustomUserDetailsService userDetailsService;

    public EmployeeRestController(EmployeeDetailsService employeeDetailsService, CustomUserDetailsService userDetailsService) {
        this.employeeDetailsService = employeeDetailsService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/")
    public List<EmployeeDetails> getAllEmployees(){
        return employeeDetailsService.findAll();
    }

    @GetMapping("/{emp_id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER') or " +
            "@securityService.isEmployeeIdCorrect(#emp_id, principal)")
    public EmployeeDetails getEmployeeById(@PathVariable Integer emp_id) throws UsernameNotFoundException, EmployeeNotFoundException {
        return employeeDetailsService.findById(emp_id);
    }

    @GetMapping("/departments")
    public List<EmployeeDetails> getEmployeeDepartments(@RequestParam String departmentName) {
        return employeeDetailsService.findByDepartment(departmentName);
    }

    @PostMapping("/add/{user_id}")
    public EmployeeDetails addEmployee(@RequestBody EmployeeDetails employeeDetails, @PathVariable Integer user_id) throws UserNotFoundException {
        User user = userDetailsService.getUserById(user_id);
        employeeDetails.setUser(user);
        return employeeDetailsService.addEmployee(employeeDetails);
    }

    @PutMapping("/update")
    public EmployeeDetails updateEmployee(@RequestBody EmployeeDetails employeeDetails) throws EmployeeNotFoundException {
        EmployeeDetails employeeDetails1 = employeeDetailsService.findById(employeeDetails.getId());
        employeeDetails.setUser(employeeDetails1.getUser());
        return employeeDetailsService.updateEmployee(employeeDetails);
    }

    @DeleteMapping("delete/{emp_id}")
    public void deleteEmployee(@PathVariable Integer emp_id) throws EmployeeNotFoundException {
        EmployeeDetails employeeDetails = employeeDetailsService.findById(emp_id);
        employeeDetailsService.delete(employeeDetails);
    }
}
