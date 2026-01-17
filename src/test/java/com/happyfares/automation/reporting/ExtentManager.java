package com.happyfares.automation.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import java.io.File;

public class ExtentManager {

    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> currentTest = new ThreadLocal<>();

    public static ExtentReports getExtent() {
        if (extent == null) {
            String reportDir = System.getProperty("user.dir") + "/src/test/reports";
            new File(reportDir).mkdirs();
            ExtentSparkReporter spark = new ExtentSparkReporter(reportDir + "/ExtenReport.html");
            extent = new ExtentReports();
            extent.attachReporter(spark);
        }
        return extent;
    }

    public static void setTest(ExtentTest test) {
        currentTest.set(test);
    }

    public static ExtentTest getTest() {
        return currentTest.get();
    }

    public static void clearTest() {
        currentTest.remove();
    }

}
