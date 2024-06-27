package com.example.Get.Employee.Details;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.Get.Employee.Details.dto.EmployeeDto;
import com.example.Get.Employee.Details.service.ClientService;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@SpringBootTest
public class ClientServiceIntegrationTest {

    @Autowired
    private ClientService clientService;

    private static WireMockServer wireMockServer;

    @BeforeAll
    public static void startWireMockServer() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
    }

    @AfterAll
    public static void stopWireMockServer() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @BeforeEach
    public void setUp() {
        wireMockServer.resetAll();
        WebClient webClient = WebClient.create("http://localhost:" + wireMockServer.port());
        clientService = new ClientService(webClient);
    }

    @Test
    public void testGetAllEmployeeDetails() {
        wireMockServer.stubFor(get(urlEqualTo("/employee"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                "[{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@example.com\"}]")));

        List<EmployeeDto> employees = clientService.getAllEmployeeDetails();
        assertNotNull(employees);
        assertEquals(1, employees.size());
        assertEquals("John", employees.get(0).getFirstName());
    }

    @Test
    public void testCreateEmployee() {
        String expectedResponse = "{ \"empNo\": 1, \"firstName\": \"John\", \"lastName\": \"Doe\", \"email\": \"thanu.bown@example.com\" }";
        String expectedRequest = "{ \"firstName\": \"John\", \"lastName\": \"Doe\", \"email\": \"john.doe@example.com\" }";
        EmployeeDto employeeDto = new EmployeeDto("John", "Doe", "john.doe@example.com");
        wireMockServer.stubFor(post(urlEqualTo("/employee"))
                .withRequestBody(equalToJson(expectedRequest, true, true)) // Added tolerance for order and whitespace
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(expectedResponse)));

        EmployeeDto createdEmployee = clientService.createEmployee(employeeDto);
        assertNotNull(createdEmployee);
        assertEquals("safari", createdEmployee.getLastName());
        assertEquals("thanu.bown@example.com", createdEmployee.getEmail());
    }
}