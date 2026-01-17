package com.happyfares.automation.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class JavaScriptUtil {

    public static void clickByjs(WebDriver driver, String script) {
        ((JavascriptExecutor) driver).executeScript(script);
    }

    public static void ScrollToElement(WebDriver driver , WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "argumnets[0].scrollIntoView({behaviour:'smooth',block:'start'});",
                element
        );
    }
}
