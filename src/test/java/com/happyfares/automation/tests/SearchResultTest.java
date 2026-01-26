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
public class SearchResultTest extends BaseTest {
    //---------Test Data ------------
    String fromCity = "Delhi";
    String toCity = "Mumbai";
    LocalDate traveldate = LocalDate.of(2026, 6, 21);
    private final long targetPrice = 25384;

    //=========================================
    // VALIDATE SEARCHED ROUTE AND DATE with HomePage
    //=========================================

    @Test(retryAnalyzer = RetryAnalyzer.class, priority = 1, groups = {"regression"})
    public void validateSearchRouteAndDate() throws InterruptedException {
        HomePage home = new HomePage(WebDriverFactory.getDriver());
        HappyFaresCalendar calendar = new HappyFaresCalendar(WebDriverFactory.getDriver());
        SearchResultsPage results = new SearchResultsPage(WebDriverFactory.getDriver());

        //Steps
        home.searchFlight(fromCity, toCity, traveldate, calendar);
        Assert.assertTrue(WebDriverFactory.getDriver().getCurrentUrl().contains("flights"), "Did not navigate to Search Results page");

        Assert.assertTrue(results.verifyFLightsPresent(), "No Flights on Search Page ");
        results.validateSearchedRouteAndDate();
        ExtentManager.getTest().pass("Search result correctly reflects route" + fromCity + " -> " + toCity + " and deparature date " + traveldate);


    }
    @Test(retryAnalyzer = RetryAnalyzer.class, priority = 2, groups = {"regression"})
  public void ValidateFLightSelectSelectionByExactPrice() throws InterruptedException {
        HomePage home = new HomePage(WebDriverFactory.getDriver());
        HappyFaresCalendar calendar = new HappyFaresCalendar(WebDriverFactory.getDriver());
        SearchResultsPage results = new SearchResultsPage(WebDriverFactory.getDriver());

        //Steps
        home.searchFlight(fromCity, toCity, traveldate, calendar);
        Assert.assertTrue(WebDriverFactory.getDriver().getCurrentUrl().contains("flights"), "Did not navigate to Search Results page");

        Assert.assertTrue(results.verifyFLightsPresent(), "No Flights on Search Page ");
        long selectedPrice = results.selectFlightEqualToPrice(targetPrice);
        Assert.assertEquals(selectedPrice, targetPrice ,"Selected flight price doesnot match expected price ");

        ExtentManager.getTest().pass("Flight selected successfully with exact price " + targetPrice);

  }
    @Test(retryAnalyzer = RetryAnalyzer.class, priority = 3, groups = {"regression"})
    public void PriceSortedAscendingByDefault() throws InterruptedException {
        HomePage home = new HomePage(WebDriverFactory.getDriver());
        HappyFaresCalendar calendar = new HappyFaresCalendar(WebDriverFactory.getDriver());
        SearchResultsPage results = new SearchResultsPage(WebDriverFactory.getDriver());

        //Steps
        home.searchFlight(fromCity, toCity, traveldate, calendar);
        Assert.assertTrue(WebDriverFactory.getDriver().getCurrentUrl().contains("flights"), "Did not navigate to Search Results page");
        Assert.assertTrue(results.verifyFLightsPresent(), "No Flights on Search Page ");

        Assert.assertTrue(results.isPriceSortedAscendingByDefault(),"Flight Price is Not in Ascending..");

  }

    @Test(retryAnalyzer = RetryAnalyzer.class, priority = 4, groups = {"regression"})
    public void PriceSortedDescendingAfterPriceFilter() throws InterruptedException {
        HomePage home = new HomePage(WebDriverFactory.getDriver());
        HappyFaresCalendar calendar = new HappyFaresCalendar(WebDriverFactory.getDriver());
        SearchResultsPage results = new SearchResultsPage(WebDriverFactory.getDriver());

        //Steps
        home.searchFlight(fromCity, toCity, traveldate, calendar);
        Assert.assertTrue(WebDriverFactory.getDriver().getCurrentUrl().contains("flights"), "Did not navigate to Search Results page");
        Assert.assertTrue(results.verifyFLightsPresent(), "No Flights on Search Page ");

        Assert.assertTrue(results.isPriceSortedDescendingAfterPriceFilter(),"Flight Price is Not in Ascending..");

    }



}
