package com.happyfares.automation.core;

import org.testng.annotations.BeforeSuite;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class TestSuiteInitializer {
    protected static Properties config = new Properties();

    @BeforeSuite
    public void beforeSuite() throws IOException {
        FileInputStream fis = new FileInputStream("src/test/resources/config.properties");
        config.load(fis);
    }

}
