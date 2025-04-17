package org.hbn.flowcheck;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hbn.flowcheck.utils.ExcelReader;
import org.hbn.flowcheck.utils.ConfigReader;
import org.hbn.flowcheck.utils.ExcelWriter;
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
        RestAssured.baseURI = ConfigReader.getBaseURI();
    }

    @Test
    @Tag("component")
    @DisplayName("Test Case: API Tests from Excel")
    @Description("This test runs all API tests from the Excel data")
    public void testApiRequestsFromExcel() {
        String excelPath = ConfigReader.getExcelPath();
        List<Map<String, String>> testCases = ExcelReader.getTestData("Sheet1", excelPath);

        int executedTestCases = 0;
        int rowIndex = 1;

        for (Map<String, String> testCase : testCases) {
            String method = testCase.get("method");
            String endpoint = testCase.get("endpoint");
            String header = testCase.get("header");
            String body = testCase.get("body");
            int expectedStatus = (int) Double.parseDouble(testCase.get("status"));

            executedTestCases++;
            String result;

            try {
                runApiTest(method, endpoint, header, body, expectedStatus, executedTestCases);
                result = "Pass";
            } catch (AssertionError | Exception e) {
                result = "Fail";
                System.out.println("❌ Test case failed at row " + rowIndex + ": " + e.getMessage());
            }

            ExcelWriter.writeTestResult(excelPath, "Sheet1", rowIndex, result);
            rowIndex++;
        }

        System.out.println("✅ Total Test Cases Executed: " + executedTestCases);
    }

    @Step("Running API Test for method: {0} on endpoint: {1}, Test Case No: {2}")
    private void runApiTest(String method, String endpoint, String header, String body, int expectedStatus, int testCaseNumber) {
        var request = given();

        if (header != null && !header.isEmpty()) {
            request.contentType(ContentType.fromContentType(header));
        }

        if (body != null && !body.isEmpty() && !(method.equalsIgnoreCase("GET") || method.equalsIgnoreCase("DELETE"))) {
            request.body(body);
        }

        Response response;

        switch (method.toUpperCase()) {
            case "POST":
                response = request.when().post(endpoint);
                break;
            case "GET":
                response = request.when().get(endpoint);
                break;
            case "PUT":
                response = request.when().put(endpoint);
                break;
            case "DELETE":
                response = request.when().delete(endpoint);
                break;
            default:
                throw new IllegalArgumentException("Unsupported method: " + method);
        }

        // Console Output
        System.out.println("---------- Test Case #" + testCaseNumber + " ----------");
        System.out.println("Method: " + method);
        System.out.println("Endpoint: " + endpoint);
        System.out.println("Expected Status: " + expectedStatus);
        System.out.println("Actual Status: " + response.getStatusCode());
        System.out.println("Response Body:\n" + response.getBody().asPrettyString());
        System.out.println("----------------------------------------------");

        // Assertion
        assertEquals(expectedStatus, response.getStatusCode());
    }
}