package com.happyfares.automation.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class WaitUtil {

    public static WebElement waitForVisible(WebDriver driver, WebDriver locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.visibilityOfElementLocated((By) locator));
    }

    public static WebElement waitForVisible(WebDriver driver, By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.visibilityOfElementLocated((By) locator));
    }

    public static boolean waitForVisible(WebDriver driver, String partialUrl) {
        return new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.urlContains(partialUrl));
    }

    public static void waitForAngularToFinish(WebDriver driver) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        wait.until(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    return (Boolean) js.executeScript(
                            "return (window.angular !== undefined) && " +
                                    "(angular.element(document.body).injector() !== undefined) && " +
                                    "(angular.element(document.body).injector().get('$http').pendingRequests.length === 0);"
                    );
                } catch (Exception e) {
                    // If page is non-Angular or injector not ready yet
                    return true;
                }
            }
        });
    }

    public static WebElement waitForClickable(WebDriver driver, By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.elementToBeClickable(locator));
    }
}
