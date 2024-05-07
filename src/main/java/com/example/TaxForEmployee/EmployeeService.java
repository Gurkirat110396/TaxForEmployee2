package com.example.TaxForEmployee;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface EmployeeService {
    void saveEmployee(Employee employee);
    List<Employee> getAllEmployees();
    Employee getEmployeeById(Long id);
    List<TaxDeductionDTO> getTaxDeductionForCurrentYear();
}