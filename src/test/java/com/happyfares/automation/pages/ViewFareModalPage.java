package com.happyfares.automation.pages;

import com.happyfares.automation.reporting.ExtentManager;
import com.happyfares.automation.utils.JavaScriptUtil;
import com.happyfares.automation.utils.WaitUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViewFareModalPage {
    private WebDriver driver;

    //------------------Locators ----------------------------

    private By headerTitle = By.xpath("(//div[contains(@class,\"d-inline float-left\")])[5]");
    private By routeLine = By.xpath(".//p[1]");
    private By flightInfoLine = By.xpath(".//p[2]");

    private By fareTitles = By.xpath(".//div[contains(@class,\"text-white text-uppercase\")]");
    private By fareCards = By.xpath("//div[contains(@ng-init, \"PromoAmount\")]");
    private By bookNowBtn = By.xpath(".//button[contains(text(),\"Book Now\")]");
    private By nextArrow = By.xpath("(//a[contains(@class,'carousel-control-next')])[2]");


    // -----------------------------------

    public ViewFareModalPage(WebDriver driver) {
        this.driver = driver;
    }

    //-----------------------------------------------
    public void validateFareCards() {

        List<WebElement> cards = waitForModal();
        List<String> allFareTitles = new ArrayList<>();

        readVisibleFareTitles(allFareTitles);

        //debug output

        System.out.println("All fare titles found accross crousel: ");

        for (String t : allFareTitles) {
            System.out.println(" --> " + t);
        }

        //validation
        boolean saver = false;
        boolean HAPPYFARE = false;
        //placeholder for further parameters

        for (String title : allFareTitles) {
            String t = title.toUpperCase();
            if (t.contains("SAVERS")) {
                saver = true;
            }

            if (t.contains("HAPPY FARE (EASY REFUND)")) {
                HAPPYFARE = true;
            }
            //placeholder for further paramets validation
        }
        if (!(saver && HAPPYFARE)) {
            throw new AssertionError("Fare cards missing after crousel navigation " +
                    "Found fares: " + allFareTitles);
        }
        ExtentManager.getTest().pass("All fare cards validated after crousel navigation: " + allFareTitles);


    }


    //========================
    //vallidated Route and Flight Name
    //========================

    public void validateFlightDetailsHeader(String expectedFrom, String expectedTo, LocalDate expectedDate, String expectedFlightName) {
        WebElement header = driver.findElement(headerTitle);

        String routeText = header.findElement(routeLine).getText().trim();
        String infoText = header.findElement(flightInfoLine).getText().trim();

        System.out.println("Route line fetched: " + routeText);
        System.out.println("FLight info Line --> " + infoText);

        //-------------Route Validation

        if (!routeText.toLowerCase().contains(expectedFrom.toLowerCase())) {
            throw new AssertionError("From mismatch in modal header.\nExpected:\n" + expectedFrom +
                    "\nFound:\n" + routeText);
        }
        if (!routeText.toLowerCase().contains(expectedTo.toLowerCase())) {
            throw new AssertionError("To mismatch in modal header.\nExpected:\n" + expectedTo +
                    "\nFound:\n" + routeText);
        }

        //-----Date Validation-----------------
        DateTimeFormatter uiFormat = DateTimeFormatter.ofPattern("dd MMM yyy", Locale.ENGLISH);

        String expectedDateStr = expectedDate.format(uiFormat);

        if (!infoText.contains(expectedDateStr)) {
            throw new AssertionError("Date mismatch in modal header.\nExoected:\n" + expectedDateStr +
                    "\nFound:\n" + infoText);
        }

        //-----------Flight Name Validation

        Pattern pattern = Pattern.compile("[A-Z]{2}\\s*-\\s*\\d{3,4}");
        Matcher expectedMatcher = pattern.matcher(expectedFlightName);
        Matcher actualMatcher = pattern.matcher(infoText);

        if (!expectedMatcher.find()) {
            throw new AssertionError("Unable to extract flight number from expected text\n" + expectedFlightName);
        }

        if (!actualMatcher.find()) {
            throw new AssertionError("Unable to extract flight number from Modal header\n" + infoText);

        }

        String expectedFlightCode = expectedMatcher.group().replaceAll("\\s+", "");
        String actualFlightcode = actualMatcher.group().replaceAll("\\s+", "");

        System.out.println("Expected flight code -->" + expectedFlightCode);
        System.out.println("Actual flight code --> " + actualFlightcode);

        if (!expectedFlightCode.equalsIgnoreCase(actualFlightcode)) {
            throw new AssertionError("FLIGHT mismatch in modal header.\nExpected:\n" + expectedFlightCode +
                    "\nFound:\n" + actualFlightcode);
        }

        ExtentManager.getTest().pass("Modal header validated successfully " +
                "Route:\n" + expectedFrom + " --> " + expectedTo + "\nDate:\n" + expectedDateStr +
                "\nflight code in Modal Card:\n" + actualFlightcode + "Flight clicked on SearchPage:\n" + expectedFlightCode);
    }

    //=========================
    // clicking on Saver /PROMO/HappyFare BookBtn
    //==========================

    public void clickBookNow(String value) {

        // Safety limit for carousel traversal
        for (int swipe = 0; swipe < 10; swipe++) {

            List<WebElement> cards = driver.findElements(fareCards);

            if (cards.isEmpty()) {
                throw new AssertionError("No fare cards found in View Fare modal");
            }

        for (WebElement card : cards) {

            List<WebElement> titleElements = card.findElements(fareTitles);

            if (titleElements.isEmpty()) {
                continue; // skip non-fare / placeholder cards
            }

            String title = titleElements.get(0).getText().trim();
            if (title.isEmpty()) {
                continue;
            }
            System.out.println("Fare card found --> " + title);

            if (title.equalsIgnoreCase(value)) {

                WebElement bookNow = card.findElement(bookNowBtn);

                // Soft scroll to prevent modal auto-close
                JavaScriptUtil.ScrollToElement(driver, bookNow);

                bookNow.click();
                boolean urlMatched = WaitUtil.waitForVisible(driver, "/FlightBooking/refno=");

                String currentUrl = driver.getCurrentUrl();
                System.out.println("Navigated Booking URL --> " + currentUrl);

                if (!urlMatched) {
                    throw new AssertionError(
                            "Booking page URL is incorrect. Current URL: " + currentUrl
                    );
                }

                ExtentManager.getTest().pass("Booking page loaded successfully. URL validated: " + currentUrl);
                System.out.println("Clicked Book Now for SAVER fare");
                return;
            }
        }
        // ===== Carousel navigation logic (added safely) =====

        List<WebElement> arrows = driver.findElements(nextArrow);
        if (arrows.isEmpty()) {
            break; // no more carousel movement possible
        }

        WebElement arrow = arrows.get(0);

        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;

            js.executeScript(
                    "arguments[0].scrollIntoView({block:'center', inline:'center'});",
                    arrow
            );

            Thread.sleep(300); // allow animation to settle
            js.executeScript("arguments[0].click();", arrow);

            waitForReRender();

        } catch (Exception e) {
            break; // carousel cannot move further
        }
    }

        throw new AssertionError(value + " card not found. Cannot click Book Now");
    }


    // =====================================================
    //                  Helper
    // =====================================================

    private List<WebElement> waitForModal() {
        List<WebElement> cards;

        for (int i = 0; i < 20; i++) {
            cards = driver.findElements(fareCards);
            if (!cards.isEmpty()) {
                return cards;
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        throw new RuntimeException("No flight cards found on search page");
    }

    //===========================
    // Helper method to read the cards
    //============================

    private void readVisibleFareTitles(List<String> collector) {
        List<WebElement> titles = driver.findElements(fareTitles);

        for (WebElement el : titles) {
            String text = el.getText().replace("\n", " ").trim();

            if (!titles.isEmpty() && !collector.contains(text)) {
                collector.add(text);
            }
        }
    }

    private void waitForReRender() {
        try {
            Thread.sleep(400); // minimal, controlled, UI-driven
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
