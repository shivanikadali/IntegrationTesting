package com.example.Get.Employee.Details.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Get.Employee.Details.dto.EmployeeDto;
import com.example.Get.Employee.Details.service.ClientService;

@RestController
@RequestMapping("/employee")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public List<EmployeeDto> getAllEmployees() {
        return clientService.getAllEmployeeDetails();
    }

    @PostMapping
    public EmployeeDto createEmployee(@RequestBody EmployeeDto employeeDto) {
        return clientService.createEmployee(employeeDto);
    }
}