package org.hbn.flowcheck;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hbn.flowcheck.utils.ExcelReader;
import org.hbn.flowcheck.utils.ConfigReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.DisplayName;
import io.qameta.allure.Description;
import io.qameta.allure.Step;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExcelDrivenApiIntegrationTest {

    @BeforeAll
    public static void setUp() {
        // Get the base URI from the configuration file
        RestAssured.baseURI = ConfigReader.getBaseURI();
    }

    @Test
    @Tag("component")
    @DisplayName("Test Case: API Tests from Excel")
    @Description("This test runs all API tests from the Excel data")
    public void testApiRequestsFromExcel() {
        // Get the Excel path from the configuration file
        String excelPath = ConfigReader.getExcelPath();
        List<Map<String, String>> testCases = ExcelReader.getTestData("Sheet1", excelPath);  // Read all test cases

        // Start a counter to track how many test cases were executed
        int executedTestCases = 0;

        for (Map<String, String> testCase : testCases) {
            // Extract each test case's details
            String method = testCase.get("method");
            String endpoint = testCase.get("endpoint");
            String header = testCase.get("header");
            String body = testCase.get("body");
            int expectedStatus = (int) Double.parseDouble(testCase.get("status"));

            // Run API test for this particular case and report each test case separately
            executedTestCases++;
            runApiTest(method, endpoint, header, body, expectedStatus, executedTestCases);
        }

        // Print the total number of test cases executed in the report
        System.out.println("Total Test Cases Executed: " + executedTestCases);
    }

    @Step("Running API Test for method: {0} on endpoint: {1}, Test Case No: {2}")
    private void runApiTest(String method, String endpoint, String header, String body, int expectedStatus, int testCaseNumber) {
        // Prepare the request
        var request = given();

        if (header != null && !header.isEmpty()) {
            request.contentType(ContentType.fromContentType(header));
        }

        if (body != null && !body.isEmpty() && !(method.equalsIgnoreCase("GET") || method.equalsIgnoreCase("DELETE"))) {
            request.body(body);
        }

        // Execute the request based on the method
        switch (method.toUpperCase()) {
            case "POST":
                request.when().post(endpoint).then().statusCode(expectedStatus);
                // Assert the response
                assertEquals(expectedStatus, request.when().post(endpoint).getStatusCode());
                break;
            case "GET":
                request.when().get(endpoint).then().statusCode(expectedStatus);
                // Assert the response
                assertEquals(expectedStatus, request.when().get(endpoint).getStatusCode());
                break;
            case "PUT":
                request.when().put(endpoint).then().statusCode(expectedStatus);
                // Assert the response
                assertEquals(expectedStatus, request.when().put(endpoint).getStatusCode());
                break;
            case "DELETE":
                request.when().delete(endpoint).then().statusCode(expectedStatus);
                // Assert the response
                assertEquals(expectedStatus, request.when().delete(endpoint).getStatusCode());
                break;
            default:
                throw new IllegalArgumentException("Unsupported method: " + method);
        }
    }
}
