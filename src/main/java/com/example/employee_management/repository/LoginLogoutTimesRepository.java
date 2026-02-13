package com.example.employee_management.repository;

import com.example.employee_management.entity.LoginLogoutTimes;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginLogoutTimesRepository extends CrudRepository<LoginLogoutTimes, Integer> {

    @Query("SELECT llt from LoginLogoutTimes llt where llt.employeeDetails.id = :id")
    List<LoginLogoutTimes> getRecordsByEmployeeId(@Param("id") Integer id);

    @Query("SELECT llt from LoginLogoutTimes llt where llt.employeeDetails.department = :department")
    List<LoginLogoutTimes> findByDepartment(@Param("department")  String department);
}
