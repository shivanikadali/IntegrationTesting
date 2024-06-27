package com.example.Get.Employee.Details.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.Get.Employee.Details.dto.EmployeeDto;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class ClientService {

    @Autowired
    private WebClient webClientBean;

    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);

    public List<EmployeeDto> getAllEmployeeDetails() {
        return webClientBean.get()
                .uri("/employee")
                .retrieve()
                .bodyToFlux(EmployeeDto.class).collectList().block();
    }

    public EmployeeDto createEmployee(EmployeeDto employeeDto) {
        logger.info("Creating employee with data: {}", employeeDto);
        // response
        return webClientBean.post()
                .uri("/employee")
                .body(Mono.just(employeeDto), EmployeeDto.class)
                // .bodyValue(employeeDto)
                .retrieve()
                .bodyToMono(EmployeeDto.class).block();
    }
}