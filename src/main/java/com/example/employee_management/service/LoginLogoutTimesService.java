package com.example.employee_management.service;

import com.example.employee_management.entity.EmployeeDetails;
import com.example.employee_management.entity.LoginLogoutTimes;
import com.example.employee_management.exceptions.EmployeeNotFoundException;
import com.example.employee_management.repository.EmployeeRepository;
import com.example.employee_management.repository.LoginLogoutTimesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LoginLogoutTimesService {
    private final LoginLogoutTimesRepository loginLogoutTimesRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public LoginLogoutTimesService(LoginLogoutTimesRepository loginLogoutTimesRepository, EmployeeRepository employeeRepository) {
        this.loginLogoutTimesRepository = loginLogoutTimesRepository;
        this.employeeRepository = employeeRepository;
    }

    public LoginLogoutTimes save(LoginLogoutTimes loginLogoutTimes){
        return loginLogoutTimesRepository.save(loginLogoutTimes);
    }

    public List<LoginLogoutTimes> findAll(){
        return (List<LoginLogoutTimes>) loginLogoutTimesRepository.findAll();
    }

    public LoginLogoutTimes findById(Integer id) throws IllegalArgumentException {
        Optional<LoginLogoutTimes> loginLogoutTimes = loginLogoutTimesRepository.findById(id);
        if (loginLogoutTimes.isPresent()){
            return loginLogoutTimes.get();
        }
        throw new IllegalArgumentException("Login logout Time Record not found for given id");
    }

    public List<LoginLogoutTimes> findByEmployeeId(Integer id) throws EmployeeNotFoundException {
        Optional<EmployeeDetails> employee = employeeRepository.findById(id);
        if (employee.isEmpty()){
            throw new EmployeeNotFoundException("Employee Not Found with given id");
        }
        return loginLogoutTimesRepository.getRecordsByEmployeeId(id);
    }

    public List<LoginLogoutTimes> findByDepartment(String department){
        return loginLogoutTimesRepository.findByDepartment(department);
    }
}
