package co.istad.restfulemployeeapi.service.impl;

import co.istad.restfulemployeeapi.domain.Employee;
import co.istad.restfulemployeeapi.dto.CreateEmployeeRequest;
import co.istad.restfulemployeeapi.dto.EmployeeResponse;
import co.istad.restfulemployeeapi.dto.UpdateEmployeeRequest;
import co.istad.restfulemployeeapi.mapper.EmployeeMapper;
import co.istad.restfulemployeeapi.repository.EmployeeRepository;
import co.istad.restfulemployeeapi.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repository;
    private final EmployeeMapper employeeMapper;

    private int nextId = 3;

    @Override
    public List<EmployeeResponse> getAll() {
        return repository.getEmployees()
                .stream()
                .map(employeeMapper::toResponse)
                .toList();
    }

    @Override
    public List<EmployeeResponse> searchByName(String name) {
        String searchTerm = name.trim().toLowerCase(Locale.ROOT);
        if (searchTerm.isEmpty()) {
            return List.of();
        }

        return repository.getEmployees()
                .stream()
                .filter(employee -> employee.getName().toLowerCase(Locale.ROOT).contains(searchTerm))
                .map(employeeMapper::toResponse)
                .toList();
    }

    @Override
    public EmployeeResponse getEmployeeById(Integer id) {
        return employeeMapper.toResponse(findEmployeeById(id));
    }

    @Override
    public EmployeeResponse createEmployee(CreateEmployeeRequest employeeRequest) {

        if (checkIfEmailExists(employeeRequest.email())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already exists"
            );
        }

        Employee employee = employeeMapper.fromCreateEmployeeRequest(employeeRequest);
        employee.setId(nextId++);
        employee.setIsActive(Boolean.TRUE);

        repository.getEmployees().add(employee);

        return employeeMapper.toResponse(employee);
    }

    @Override
    public EmployeeResponse updateEmployeeById(Integer id, UpdateEmployeeRequest updateEmployeeRequest) {
        Employee employee = findEmployeeById(id);
        if (checkIfEmailExistsForAnotherEmployee(updateEmployeeRequest.email(), id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already exists"
            );
        }

        employee.setName(updateEmployeeRequest.name());
        employee.setEmail(updateEmployeeRequest.email());
        employee.setSalary(updateEmployeeRequest.salary());
        employee.setHireDate(updateEmployeeRequest.hireDate());

        return employeeMapper.toResponse(employee);
    }

    @Override
    public void deleteEmployeeById(Integer id) {
        Employee employee = findEmployeeById(id);
        repository.getEmployees().remove(employee);
    }

    private boolean checkIfEmailExists(String email) {
        return email != null && repository.getEmployees()
                .stream()
                .anyMatch(emp -> email.equals(emp.getEmail()));
    }

    private boolean checkIfEmailExistsForAnotherEmployee(String email, Integer id) {
        return email != null && repository.getEmployees()
                .stream()
                .anyMatch(employee -> !employee.getId().equals(id) && email.equals(employee.getEmail()));
    }

    private Employee findEmployeeById(Integer id) {
        return repository.getEmployees()
                .stream()
                .filter(employee -> employee.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Employee with id: %d not found".formatted(id)
                ));
    }

}
