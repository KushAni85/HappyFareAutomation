package com.happyfares.automation.core;

import org.testng.annotations.BeforeTest;

public class TestConfiguration extends TestSuiteInitializer{

    protected static String browser;
    protected static String baseUrl;

    @BeforeTest
    public void beforeTest() {
        browser = config.getProperty("browser");
        baseUrl = config.getProperty("base.url");
    }
    ///  Future scope --> write code for reading the key and value from properties class
}
