package com.happyfares.automation.core;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest extends TestConfiguration {

    @BeforeMethod
    public void setUp() {
        WebDriverFactory.initDriver(browser);
        WebDriverFactory.getDriver().manage().window().maximize();
        WebDriverFactory.getDriver().get(browser);
    }

    @AfterMethod
    public void tearDown() {
        WebDriverFactory.quitDriver();
    }
}
