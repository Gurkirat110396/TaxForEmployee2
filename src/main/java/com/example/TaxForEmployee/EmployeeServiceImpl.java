package com.example.TaxForEmployee;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService{
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void saveEmployee(Employee employee) {
        employeeRepository.save(employee);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + id));
    }

    @Override
    public List<TaxDeductionDTO> getTaxDeductionForCurrentYear() {
        List<Employee> employees = getAllEmployees();
        List<TaxDeductionDTO> taxDeductionDTOs = new ArrayList<>();

        for (Employee employee : employees) {
            BigDecimal totalSalary = calculateTotalSalaryForCurrentYear(employee);
            BigDecimal taxAmount = calculateTaxAmount(totalSalary);
            BigDecimal cessAmount = calculateCessAmount(totalSalary);

            TaxDeductionDTO taxDeductionDTO = new TaxDeductionDTO();
            taxDeductionDTO.setEmployeeCode(employee.getEmployeeId());
            taxDeductionDTO.setFirstName(employee.getFirstName());
            taxDeductionDTO.setLastName(employee.getLastName());
            taxDeductionDTO.setYearlySalary(totalSalary);
            taxDeductionDTO.setTaxAmount(taxAmount);
            taxDeductionDTO.setCessAmount(cessAmount);

            taxDeductionDTOs.add(taxDeductionDTO);
        }

        return taxDeductionDTOs;
    }

    private BigDecimal calculateTotalSalaryForCurrentYear(Employee employee) {
        LocalDate doj = employee.getDoj();
        BigDecimal salary = employee.getSalary();

        LocalDate startOfFinancialYear = LocalDate.of(doj.getYear(), Month.APRIL, 1);
        LocalDate endOfFinancialYear = startOfFinancialYear.plusYears(1);

        if (doj.isAfter(startOfFinancialYear) && doj.isBefore(endOfFinancialYear)) {
            long monthsWorked = ChronoUnit.MONTHS.between(doj, endOfFinancialYear);
            BigDecimal totalSalary = salary.multiply(BigDecimal.valueOf(monthsWorked)).
                    divide(BigDecimal.valueOf(12), RoundingMode.HALF_UP);
            return totalSalary;
        } else {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal calculateTaxAmount(BigDecimal totalSalary) {
        BigDecimal taxAmount = BigDecimal.ZERO;

        if (totalSalary.compareTo(BigDecimal.valueOf(250000)) > 0) {
            BigDecimal slab1Tax = BigDecimal.valueOf(250000).multiply(BigDecimal.valueOf(0.05));
            taxAmount = taxAmount.add(slab1Tax);

            if (totalSalary.compareTo(BigDecimal.valueOf(500000)) > 0) {
                BigDecimal slab2Tax = (BigDecimal.valueOf(500000).subtract(BigDecimal.valueOf(250000))).multiply(BigDecimal.valueOf(0.10));
                taxAmount = taxAmount.add(slab2Tax);

                if (totalSalary.compareTo(BigDecimal.valueOf(1000000)) > 0) {
                    BigDecimal slab3Tax = (BigDecimal.valueOf(1000000).subtract(BigDecimal.valueOf(500000))).multiply(BigDecimal.valueOf(0.20));
                    taxAmount = taxAmount.add(slab3Tax);

                    BigDecimal slab4Tax = (totalSalary.subtract(BigDecimal.valueOf(1000000))).multiply(BigDecimal.valueOf(0.30));
                    taxAmount = taxAmount.add(slab4Tax);
                } else {
                    BigDecimal slab3Tax = (totalSalary.subtract(BigDecimal.valueOf(500000))).multiply(BigDecimal.valueOf(0.20));
                    taxAmount = taxAmount.add(slab3Tax);
                }
            } else {
                BigDecimal slab2Tax = (totalSalary.subtract(BigDecimal.valueOf(250000))).multiply(BigDecimal.valueOf(0.10));
                taxAmount = taxAmount.add(slab2Tax);
            }
        } else {
            taxAmount = BigDecimal.ZERO;
        }

        return taxAmount;
    }

    private BigDecimal calculateCessAmount(BigDecimal totalSalary) {
        BigDecimal cessAmount = BigDecimal.ZERO;

        if (totalSalary.compareTo(BigDecimal.valueOf(2500000)) > 0) {
            cessAmount = (totalSalary.subtract(BigDecimal.valueOf(2500000))).multiply(BigDecimal.valueOf(0.02));
        }

        return cessAmount;
    }
}