package com.example.employee_management.repository;

import com.example.employee_management.entity.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role, Integer> {
    Role findRoleByName(String name);
}
