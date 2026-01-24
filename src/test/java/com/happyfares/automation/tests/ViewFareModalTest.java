package com.happyfares.automation.tests;

import com.happyfares.automation.core.BaseTest;
import com.happyfares.automation.core.WebDriverFactory;
import com.happyfares.automation.pages.HomePage;
import com.happyfares.automation.pages.SearchResultsPage;
import com.happyfares.automation.pages.ViewFareModalPage;
import com.happyfares.automation.reporting.ExtentManager;
import com.happyfares.automation.utils.HappyFaresCalendar;
import com.happyfares.automation.utils.RetryAnalyzer;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.time.LocalDate;

@Listeners(com.happyfares.automation.reporting.TestListener.class)
public class ViewFareModalTest extends BaseTest {

    //---------Test Data ------------
    String fromCity = "Delhi";
    String toCity = "Mumbai";
    LocalDate traveldate = LocalDate.of(2026, 6, 21);
    private final long targetPrice = 25384;

    @Test(retryAnalyzer = RetryAnalyzer.class, priority = 1, groups = {"regression"})
    public void validateFareCardsTitles() throws InterruptedException {
        HomePage home = new HomePage(WebDriverFactory.getDriver());
        HappyFaresCalendar calendar = new HappyFaresCalendar(WebDriverFactory.getDriver());
        SearchResultsPage results = new SearchResultsPage(WebDriverFactory.getDriver());
        ViewFareModalPage modal = new ViewFareModalPage(WebDriverFactory.getDriver());

        //Steps
        home.searchFlight(fromCity, toCity, traveldate, calendar);
        Assert.assertTrue(WebDriverFactory.getDriver().getCurrentUrl().contains("flights"), "Did not navigate to Search Results page");

        Assert.assertTrue(results.verifyFLightsPresent(), "No Flights on Search Page ");
        long selectedPrice = results.selectFlightEqualToPrice(targetPrice);
        Assert.assertEquals(selectedPrice, targetPrice ,"Selected flight price doesnot match expected price ");

        // Validation of card title
        modal.validateFareCards();
        ExtentManager.getTest().pass("Fare cards validated successfully : SAVER, HAPPYFARE");

    }

    @Test(retryAnalyzer = RetryAnalyzer.class, priority = 2, groups = {"regression"})
    public void validateFlightDetailsHeader() throws InterruptedException {
        HomePage home = new HomePage(WebDriverFactory.getDriver());
        HappyFaresCalendar calendar = new HappyFaresCalendar(WebDriverFactory.getDriver());
        SearchResultsPage results = new SearchResultsPage(WebDriverFactory.getDriver());
        ViewFareModalPage modal = new ViewFareModalPage(WebDriverFactory.getDriver());

        //Steps
        home.searchFlight(fromCity, toCity, traveldate, calendar);
        Assert.assertTrue(WebDriverFactory.getDriver().getCurrentUrl().contains("flights"), "Did not navigate to Search Results page");

        Assert.assertTrue(results.verifyFLightsPresent(), "No Flights on Search Page ");
        long selectedPrice = results.selectFlightEqualToPrice(targetPrice);
        String selectedFlightName = results.getSelectedFlightName();
        Assert.assertEquals(selectedPrice, targetPrice ,"Selected flight price doesnot match expected price ");
        //validation Headers Route and Flight details
        modal.validateFlightDetailsHeader(fromCity, toCity , traveldate , selectedFlightName);
        modal.clickBookNow("PROMO");

    }
}
