package com.happyfares.automation.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.happyfares.automation.core.WebDriverFactory;
import com.happyfares.automation.utils.ScreenshotUtil;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    private static ExtentReports extent = ExtentManager.getExtent();

    @Override
    public void onTestStart(ITestResult result) {
        if (ExtentManager.getTest() == null) {
            ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName());
            ExtentManager.setTest(extentTest);
            /*
            ExtentManager.getTest() == null => Check if current Extent Thread is already assign or null ,
            so basically This condition ensures:
            A new ExtentTest is created only once per test
            Prevents overwriting an existing test node during retries or parallel runs

          ExtentTest extentTest  => This line will creat a new test node is created in the Extent report
          The test name is taken from: @Test method name and reports shows something like this validatePriceSortingDescendingAfterFilter

          At this point:The test exists in the HTML report But it is not yet linked to the running thread
            ExtentManager.setTest(extentTest) => This line binds everything together.
            Stores extentTest in a ThreadLocal , From now on:
            ExtentManager.getTest().info(...)
            ExtentManager.getTest().pass(...)
            ExtentManager.getTest().fail(...)
          will log to this exact test
         */
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {

        try {
            ExtentManager.getTest().fail(result.getThrowable());
            String path = ScreenshotUtil.capture(WebDriverFactory.getDriver(),result.getMethod().getMethodName());
            ExtentManager.getTest().addScreenCaptureFromPath(path);
        } catch (Exception e) {
            ExtentManager.getTest().info("Screenshot not capture due to inactive browser ");
        }
        finally {
            ExtentManager.clearTest();
        }
    }


    @Override
    public void onFinish(ITestContext context) {
    extent.flush();

    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentManager.clearTest();

    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentManager.getTest().skip("Test skipped");
        ExtentManager.clearTest();
    }


}
