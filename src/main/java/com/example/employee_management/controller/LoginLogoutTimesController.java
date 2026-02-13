package com.example.employee_management.controller;

import com.example.employee_management.entity.EmployeeDetails;
import com.example.employee_management.entity.EntryType;
import com.example.employee_management.entity.LoginLogoutTimes;
import com.example.employee_management.exceptions.EmployeeNotFoundException;
import com.example.employee_management.service.EmployeeDetailsService;
import com.example.employee_management.service.LoginLogoutTimesService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/v1/times")
public class LoginLogoutTimesController {

    private final LoginLogoutTimesService loginLogoutTimesService;
    private final EmployeeDetailsService employeeDetailsService;

    public LoginLogoutTimesController(LoginLogoutTimesService loginLogoutTimesService, EmployeeDetailsService employeeDetailsService) {
        this.loginLogoutTimesService = loginLogoutTimesService;
        this.employeeDetailsService = employeeDetailsService;
    }

    @GetMapping("/")
    public List<LoginLogoutTimes> getAllLoginLogoutTimes(){
        return loginLogoutTimesService.findAll();
    }

    @GetMapping("/{record_id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER') or " +
            "@securityService.isOwner(#record_id, principal)")
    public LoginLogoutTimes getLoginLogoutTimesById(@PathVariable Integer record_id) throws UsernameNotFoundException, IllegalArgumentException {
        return loginLogoutTimesService.findById(record_id);
    }

    @GetMapping("/employee/{emp_id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER') or " +
            "@securityService.isEmployeeIdCorrect(#emp_id, principal)")
    public List<LoginLogoutTimes> getLoginLogoutTimesByEmployeeId(@PathVariable Integer emp_id) {
        return loginLogoutTimesService.findByEmployeeId(emp_id);
    }

    @PostMapping("/login/{emp_id}")
    @PreAuthorize("@securityService.isEmployeeIdCorrect(#emp_id, principal)")
    public LoginLogoutTimes loginToOffice(@RequestBody LoginLogoutTimes loginLogoutTimes, @PathVariable Integer emp_id) throws EmployeeNotFoundException {
        EmployeeDetails employeeDetails = employeeDetailsService.findById(emp_id);
        loginLogoutTimes.setEntryType(EntryType.login);
        loginLogoutTimes.setEmployeeDetails(employeeDetails);
        return loginLogoutTimesService.save(loginLogoutTimes);
    }

    @PostMapping("/logout/{emp_id}")
    @PreAuthorize("@securityService.isEmployeeIdCorrect(#emp_id, principal)")
    public LoginLogoutTimes logoutFromOffice(@RequestBody LoginLogoutTimes loginLogoutTimes, @PathVariable Integer emp_id) throws EmployeeNotFoundException {
        EmployeeDetails employeeDetails = employeeDetailsService.findById(emp_id);
        loginLogoutTimes.setEntryType(EntryType.logout);
        loginLogoutTimes.setEmployeeDetails(employeeDetails);
        return loginLogoutTimesService.save(loginLogoutTimes);
    }

    @GetMapping("/departments")
    public List<LoginLogoutTimes> getTimesByDepartment(@RequestParam String departmentName) {
        return loginLogoutTimesService.findByDepartment(departmentName);
    }
}
