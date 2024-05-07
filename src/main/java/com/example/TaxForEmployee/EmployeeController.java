package com.example.TaxForEmployee;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController{
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<Void> createEmployee(@Valid @RequestBody Employee employee) {
        employeeService.saveEmployee(employee);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/tax-deduction")
    public ResponseEntity<List<TaxDeductionDTO>> getTaxDeductionForCurrentYear() {
        List<TaxDeductionDTO> taxDeductionDTOs = employeeService.getTaxDeductionForCurrentYear();
        return ResponseEntity.ok(taxDeductionDTOs);
    }
}