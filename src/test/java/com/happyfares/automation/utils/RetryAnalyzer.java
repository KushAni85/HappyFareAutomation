package com.happyfares.automation.utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

   private int count = 0;
   private static final int maxRetry = 1;

    @Override
    public boolean retry(ITestResult result) {
        if(count < maxRetry) {
            count++;
            return true;
        }
        return false;
    }
}
