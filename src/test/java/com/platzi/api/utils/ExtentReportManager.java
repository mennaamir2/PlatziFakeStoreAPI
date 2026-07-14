package com.platzi.api.utils;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentReportManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    public static ExtentReports getInstance() {
        if (extent == null) {
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter("test-output/ExtentReport.html");
            sparkReporter.config().setTheme(Theme.DARK);
            sparkReporter.config().setDocumentTitle("Platzi Fake Store API - Test Report");
            sparkReporter.config().setReportName("API Automation Test Results");

            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
            extent.setSystemInfo("Base URL",    ConfigManager.BASE_URL);
            extent.setSystemInfo("Framework",   "Rest Assured + TestNG");
            extent.setSystemInfo("API Under Test", "Platzi Fake Store API");
            extent.setSystemInfo("Tester",      "QA Automation Team");
        }
        return extent;
    }

    public static ExtentTest createTest(String testName, String description) {
        ExtentTest test = getInstance().createTest(testName, description);
        extentTest.set(test);
        return test;
    }

    public static ExtentTest getTest() {
        return extentTest.get();
    }

    public static void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
}
