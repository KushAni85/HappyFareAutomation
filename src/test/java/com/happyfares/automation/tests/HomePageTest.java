package com.happyfares.automation.tests;

import com.happyfares.automation.core.BaseTest;
import com.happyfares.automation.core.WebDriverFactory;
import com.happyfares.automation.pages.HomePage;
import com.happyfares.automation.pages.SearchResultsPage;
import com.happyfares.automation.reporting.ExtentManager;
import com.happyfares.automation.utils.HappyFaresCalendar;
import com.happyfares.automation.utils.RetryAnalyzer;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.time.LocalDate;


@Listeners(com.happyfares.automation.reporting.TestListener.class)
public class HomePageTest extends BaseTest {

    //---------Test Data ------------
    String fromCity = "Delhi";
    String toCity = "Mumbai";

    LocalDate traveldate = LocalDate.of(2026, 6, 21);

    @Test(retryAnalyzer = RetryAnalyzer.class, priority = 1, groups = {"regression"})
    public void verifyFlightSearch() throws InterruptedException {

        //page object
        HomePage home = new HomePage(WebDriverFactory.getDriver());
        HappyFaresCalendar calendar = new HappyFaresCalendar(WebDriverFactory.getDriver());
        SearchResultsPage results = new SearchResultsPage(WebDriverFactory.getDriver());

        //Steps
        home.searchFlight(fromCity, toCity, traveldate, calendar);

        //validation
        Thread.sleep(2000);
        Assert.assertTrue(WebDriverFactory.getDriver().getCurrentUrl().contains("flights"), "Did not navigate to Search Results page");

        Assert.assertTrue(results.verifyFLightsPresent(), "No Flights on Search Page ");
        ExtentManager.getTest().pass("Flights displayed successfully from " + fromCity + " -> " + toCity);

    }
}
