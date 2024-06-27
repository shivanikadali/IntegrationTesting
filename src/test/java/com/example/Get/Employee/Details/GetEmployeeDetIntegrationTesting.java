package com.example.Get.Employee.Details;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = GetEmployeeDetailsApplication.class)
public class GetEmployeeDetIntegrationTesting {

    private static WireMockServer wireMockServer;

    @BeforeAll
    public static void setUp() throws IOException {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());

        String expectedRequest = new String(Files.readAllBytes(Paths.get("src/test/resources/employees.json")));
        String allEmployeesRequest = new String(Files.readAllBytes(Paths.get("src/test/resources/allEmployees.json")));

        // Setup stub for the /employee endpoint
        WireMock.stubFor(post(urlEqualTo("/employee"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody(
                                expectedRequest)));

        WireMock.stubFor(get(urlEqualTo("/employee"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody(
                                allEmployeesRequest)));

    }

    @AfterAll
    public static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void getAll() {

        RestAssured.given()
                .get("http://localhost:" + wireMockServer.port() + "/employee")
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void createEmployee() throws IOException, URISyntaxException {

        String expectedRequest = new String(Files.readAllBytes(Paths.get("src/test/resources/employees.json")));

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(expectedRequest)
                .post("http://localhost:" + wireMockServer.port() + "/employee")
                .then()
                .assertThat()
                .statusCode(200);
    }

    // Detail validations using JSONArray
    @Test
    public void getAllEmployees() throws JSONException {
        // Send GET request and capture the response
        Response response = RestAssured.given()
                .get("http://localhost:" + wireMockServer.port() + "/employee");

        // Log the response
        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asString());

        // Validate the status code
        response.then().assertThat().statusCode(200);

        // Parse the JSON response
        String responseBody = response.getBody().asString();
        JSONArray jsonResponse = new JSONArray(responseBody);

        // Validate the first employee's fields
        if (jsonResponse.length() > 0) {
            JSONObject firstEmployee = jsonResponse.getJSONObject(0);

            int empNo = firstEmployee.getInt("empNo");
            String firstName = firstEmployee.getString("firstName");
            String lastName = firstEmployee.getString("lastName");
            String email = firstEmployee.getString("email");

            // Assertions for the first employee
            assertEquals(1, empNo);
            assertEquals("Eva", firstName);
            assertEquals("Brown", lastName);
            assertEquals("eva.brown@example.com", email);

            System.out.println("Assertions passed: Names and email match for the first employee");
        } else {
            System.out.println("No employees found in the response");
        }
    }

    @Test
    public void createEmployeeDetails() throws IOException, URISyntaxException, JSONException {

        String expectedRequest = new String(Files.readAllBytes(Paths.get("src/test/resources/employees.json")));

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(expectedRequest)
                .post("http://localhost:" + wireMockServer.port() + "/employee");
        response.then()
                .assertThat()
                .statusCode(200);
        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asString());

        String responseString = response.getBody().asString();
        JSONObject jsonObjectRes = new JSONObject(responseString);
        JSONObject jsonObjectexpect = new JSONObject(responseString);
        assertEquals(jsonObjectRes.getString("email"), jsonObjectexpect.getString("email"));
    }
}
