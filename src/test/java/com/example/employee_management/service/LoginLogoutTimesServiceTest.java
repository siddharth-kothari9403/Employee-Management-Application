package com.example.employee_management.service;

import com.example.employee_management.entity.EmployeeDetails;
import com.example.employee_management.entity.EntryType;
import com.example.employee_management.entity.LoginLogoutTimes;
import com.example.employee_management.exceptions.EmployeeNotFoundException;
import com.example.employee_management.repository.EmployeeRepository;
import com.example.employee_management.repository.LoginLogoutTimesRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class LoginLogoutTimesServiceTest {

    @Mock
    private LoginLogoutTimesRepository loginLogoutTimesRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private LoginLogoutTimesService loginLogoutTimesService;

    private LoginLogoutTimes loginLogoutTimes1;
    private LoginLogoutTimes loginLogoutTimes2;
    private LoginLogoutTimes loginLogoutTimes3;
    private LoginLogoutTimes loginLogoutTimes4;
    private List<LoginLogoutTimes> loginLogoutTimesList;
    private List<LoginLogoutTimes> engineeringLoginLogoutTimesList;
    private List<LoginLogoutTimes> employeeLoginLogoutTimesList;
    private EmployeeDetails employeeDetails1;

    @BeforeEach
    public void setUp(){

        this.employeeDetails1 = new EmployeeDetails();
        this.employeeDetails1.setId(1);
        this.employeeDetails1.setDepartment("Engineering");

        EmployeeDetails employeeDetails2 = new EmployeeDetails();
        employeeDetails2.setId(2);
        employeeDetails2.setDepartment("HR");

        EmployeeDetails employeeDetails3 = new EmployeeDetails();
        employeeDetails3.setId(3);
        employeeDetails3.setDepartment("Engineering");

        this.loginLogoutTimesList = new ArrayList<>();
        this.employeeLoginLogoutTimesList = new ArrayList<>();
        this.engineeringLoginLogoutTimesList = new ArrayList<>();

        this.loginLogoutTimes1 = new LoginLogoutTimes();
        loginLogoutTimes1.setEntryType(EntryType.login);
        loginLogoutTimes1.setTime(Time.valueOf("08:45:00"));
        loginLogoutTimes1.setDate(Date.valueOf("2024-05-09"));
        loginLogoutTimes1.setEmployeeDetails(employeeDetails1);
        loginLogoutTimes1.setEntry_id(1);

        loginLogoutTimesList.add(loginLogoutTimes1);
        employeeLoginLogoutTimesList.add(loginLogoutTimes1);
        engineeringLoginLogoutTimesList.add(loginLogoutTimes1);

        this.loginLogoutTimes2 = new LoginLogoutTimes();
        loginLogoutTimes2.setEntryType(EntryType.logout);
        loginLogoutTimes2.setTime(Time.valueOf("09:15:00"));
        loginLogoutTimes2.setDate(Date.valueOf("2024-05-09"));
        loginLogoutTimes2.setEmployeeDetails(employeeDetails1);
        loginLogoutTimes2.setEntry_id(2);

        loginLogoutTimesList.add(loginLogoutTimes2);
        employeeLoginLogoutTimesList.add(loginLogoutTimes2);
        engineeringLoginLogoutTimesList.add(loginLogoutTimes2);

        this.loginLogoutTimes3 = new LoginLogoutTimes();
        loginLogoutTimes3.setEntryType(EntryType.login);
        loginLogoutTimes3.setTime(Time.valueOf("09:15:00"));
        loginLogoutTimes3.setDate(Date.valueOf("2024-05-09"));
        loginLogoutTimes3.setEmployeeDetails(employeeDetails2);
        loginLogoutTimes3.setEntry_id(3);

        loginLogoutTimesList.add(loginLogoutTimes3);

        this.loginLogoutTimes4 = new LoginLogoutTimes();
        loginLogoutTimes4.setEntryType(EntryType.login);
        loginLogoutTimes4.setTime(Time.valueOf("08:30:00"));
        loginLogoutTimes4.setDate(Date.valueOf("2024-05-09"));
        loginLogoutTimes4.setEmployeeDetails(employeeDetails3);
        loginLogoutTimes4.setEntry_id(4);

        loginLogoutTimesList.add(loginLogoutTimes4);
        engineeringLoginLogoutTimesList.add(loginLogoutTimes4);
    }

    @AfterEach
    public void tearDown(){
        this.loginLogoutTimesList = null;
        this.employeeLoginLogoutTimesList = null;
        this.engineeringLoginLogoutTimesList = null;
        this.loginLogoutTimes1 = null;
        this.loginLogoutTimes2 = null;
        this.loginLogoutTimes3 = null;
        this.loginLogoutTimes4 = null;
    }

    @Test
    public void givenTimeToAddShouldReturnAddedTime(){
        when(loginLogoutTimesRepository.save(any())).thenReturn(loginLogoutTimes1);
        LoginLogoutTimes loginLogoutTimes = loginLogoutTimesService.save(loginLogoutTimes1);
        assertThat(loginLogoutTimes).isEqualTo(loginLogoutTimes1);
        verify(loginLogoutTimesRepository, times(1)).save(any());
    }

    @Test
    public void givenValidIdReturnsTimesInfo(){
        when(loginLogoutTimesRepository.findById(any())).thenReturn(Optional.of(loginLogoutTimes1));
        LoginLogoutTimes loginLogoutTimes = loginLogoutTimesService.findById(1);
        assertThat(loginLogoutTimes).isEqualTo(loginLogoutTimes1);
        verify(loginLogoutTimesRepository,times(1)).findById(any());
    }

    @Test
    public void givenInvalidIdThrowsException(){
        when(loginLogoutTimesRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> loginLogoutTimesService.findById(5));
        verify(loginLogoutTimesRepository,times(1)).findById(any());
    }

    @Test
    public void returnAllTimes(){
        when(loginLogoutTimesRepository.findAll()).thenReturn(loginLogoutTimesList);
        List<LoginLogoutTimes> loginLogoutTimesList1 = loginLogoutTimesService.findAll();
        assertThat(loginLogoutTimesList1).isEqualTo(loginLogoutTimesList);
        verify(loginLogoutTimesRepository,times(1)).findAll();
    }

    @Test
    public void returnTimesByDepartment(){
        when(loginLogoutTimesRepository.findByDepartment(any())).thenReturn(engineeringLoginLogoutTimesList);
        List<LoginLogoutTimes> loginLogoutTimes1 = loginLogoutTimesService.findByDepartment("Engineering");
        assertThat(loginLogoutTimes1).isEqualTo(engineeringLoginLogoutTimesList);
        verify(loginLogoutTimesRepository,times(1)).findByDepartment(any());
    }

    @Test
    public void returnTimesByValidEmployeeIdShouldReturnValidList(){
        when(loginLogoutTimesRepository.getRecordsByEmployeeId(any())).thenReturn(employeeLoginLogoutTimesList);
        when(employeeRepository.findById(any())).thenReturn(Optional.of(this.employeeDetails1));
        List<LoginLogoutTimes> loginLogoutTimesList1 = loginLogoutTimesService.findByEmployeeId(1);
        assertThat(loginLogoutTimesList1).isEqualTo(employeeLoginLogoutTimesList);
        verify(loginLogoutTimesRepository,times(1)).getRecordsByEmployeeId(any());
        verify(employeeRepository,times(1)).findById(any());
    }

    @Test
    public void returnTimesByInvalidEmployeeIdShouldThrowException(){
        when(employeeRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(EmployeeNotFoundException.class, () -> loginLogoutTimesService.findByEmployeeId(5));
        verify(loginLogoutTimesRepository,times(0)).getRecordsByEmployeeId(any());
        verify(employeeRepository,times(1)).findById(any());
    }
}