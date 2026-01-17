package com.happyfares.automation.utils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;

public class ScreenshotUtil {

    public static  String capture(WebDriver driver , String testName) throws IOException {

        String reportDir = System.getProperty("user.dir")+ "/src/test/reports/screenshots";
        File dir = new File(reportDir);
        if(!dir.exists()) {
            dir.mkdirs();
        }

        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String filename = testName + "_" + System.currentTimeMillis()+".png";
        File dest = new File(dir, filename);
        FileUtils.copyFile(src, dest);
        return  "screenshots/" +filename;
    }
}
