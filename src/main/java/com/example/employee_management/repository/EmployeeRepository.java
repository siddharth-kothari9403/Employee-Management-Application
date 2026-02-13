package com.example.employee_management.repository;

import com.example.employee_management.entity.EmployeeDetails;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends CrudRepository<EmployeeDetails, Integer> {
    List<EmployeeDetails> findByDepartment(String department);
}
