package com.example.employee_management.service;

import com.example.employee_management.entity.EmployeeDetails;
import com.example.employee_management.entity.User;
import com.example.employee_management.exceptions.EmployeeNotFoundException;
import com.example.employee_management.exceptions.UserNotFoundException;
import com.example.employee_management.repository.EmployeeRepository;
import com.example.employee_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeDetailsService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    @Autowired
    public EmployeeDetailsService(EmployeeRepository employeeRepository, UserRepository userRepository) {
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
    }

    public EmployeeDetails updateEmployee(EmployeeDetails employeeDetails) {
        return employeeRepository.save(employeeDetails);
    }

    public EmployeeDetails addEmployee(EmployeeDetails employeeDetails) {
        return employeeRepository.save(employeeDetails);
    }

    public EmployeeDetails findById(Integer id) throws EmployeeNotFoundException {
        Optional<EmployeeDetails> employee = employeeRepository.findById(id);
        if (employee.isEmpty()){
            throw new EmployeeNotFoundException("Employee not found for given id");
        }
        return employee.get();
    }

    public List<EmployeeDetails> findAll() {
        return (List<EmployeeDetails>) employeeRepository.findAll();
    }

    public List<EmployeeDetails> findByDepartment(String department) {
        return employeeRepository.findByDepartment(department);
    }

    public void delete(EmployeeDetails employeeDetails) throws EmployeeNotFoundException {
        Optional<User> optionalUser = userRepository.findById(employeeDetails.getUser().getId());
        if (optionalUser.isEmpty()){
            throw new UserNotFoundException("Invalid user id");
        }
        User user = optionalUser.get();
        user.setEmployeeDetails(null);
        employeeRepository.delete(employeeDetails);
    }
}
