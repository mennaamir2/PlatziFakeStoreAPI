package com.platzi.api.utils;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.lang.reflect.Method;

public class BaseTest {

    protected static RequestSpecification requestSpec;
    protected static String accessToken;

    @BeforeSuite
    public void globalSetup() {
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(ConfigManager.BASE_URL)
                .setContentType("application/json")
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeMethod
    public void startTest(Method method) {
        String testName = method.getName();
        String description = method.isAnnotationPresent(org.testng.annotations.Test.class)
                ? method.getAnnotation(org.testng.annotations.Test.class).description()
                : "";
        ExtentReportManager.createTest(testName, description);
    }

    @AfterMethod
    public void endTest(ITestResult result) {
        ExtentTest test = ExtentReportManager.getTest();
        if (test != null) {
            switch (result.getStatus()) {
                case ITestResult.SUCCESS:
                    test.log(Status.PASS, "Test PASSED");
                    break;
                case ITestResult.FAILURE:
                    test.log(Status.FAIL, "Test FAILED: " + result.getThrowable());
                    break;
                case ITestResult.SKIP:
                    test.log(Status.SKIP, "Test SKIPPED: " + result.getThrowable());
                    break;
            }
        }
    }

    @AfterSuite
    public void globalTearDown() {
        ExtentReportManager.flush();
    }

    protected void log(String message) {
        ExtentTest test = ExtentReportManager.getTest();
        if (test != null) test.log(Status.INFO, message);
    }
}
