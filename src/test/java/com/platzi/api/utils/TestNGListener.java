package com.platzi.api.utils;

import com.aventstack.extentreports.Status;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestNGListener implements ITestListener {

    @Override
    public void onTestSuccess(ITestResult result) {
        if (ExtentReportManager.getTest() != null) {
            ExtentReportManager.getTest().log(Status.PASS, "PASSED");
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        if (ExtentReportManager.getTest() != null) {
            ExtentReportManager.getTest().log(Status.FAIL,
                    "FAILED: " + result.getThrowable().getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if (ExtentReportManager.getTest() != null) {
            ExtentReportManager.getTest().log(Status.SKIP,
                    "SKIPPED: " + (result.getThrowable() != null ? result.getThrowable().getMessage() : ""));
        }
    }
}
