package co.istad.restfulemployeeapi.controller;

import co.istad.restfulemployeeapi.dto.CreateEmployeeRequest;
import co.istad.restfulemployeeapi.dto.EmployeeResponse;
import co.istad.restfulemployeeapi.dto.UpdateEmployeeRequest;
import co.istad.restfulemployeeapi.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService service;

    @GetMapping
    public List<EmployeeResponse> getAllEmployees() {
        return service.getAll();
    }

    @GetMapping("/search")
    public List<EmployeeResponse> searchEmployees(@RequestParam String name) {
        return service.searchByName(name);
    }

    @GetMapping("/{id}")
    public EmployeeResponse getEmployeeById(@PathVariable Integer id) {
        return service.getEmployeeById(id);
    }

    @PostMapping
    public EmployeeResponse createEmployee(
            @Valid @RequestBody CreateEmployeeRequest createRequest
            ) {
        return service.createEmployee(createRequest);
    }

    @PutMapping("/{id}")
    public EmployeeResponse updateEmployee(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateEmployeeRequest updateRequest
    ) {
        return service.updateEmployeeById(id, updateRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployee(@PathVariable Integer id) {
        service.deleteEmployeeById(id);
    }

}
