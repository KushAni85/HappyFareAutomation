package com.happyfares.automation.pages;

import com.happyfares.automation.reporting.ExtentManager;
import com.happyfares.automation.utils.JavaScriptUtil;
import com.happyfares.automation.utils.WaitUtil;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchResultsPage {

    private WebDriver driver;
    private String selectedFlightName;

    //-------------Locators---------------------------
    private By flightCards = By.xpath("//div[@class='row ng-scope']");
    private By searchSummary = By.xpath("//span[contains(@ng-show , 'ONE_WAY')]");
    private By dateFetch =  By.xpath("//p[contains(@ng-class, 'ROUNDTRIP')]");
    private By priceCards = By.xpath("//span[contains(normalize-space(),'₹')]");
    private By priceFilter = By.xpath("//span[contains(normalize-space(),'Price')]");
    private By viewFareBtn = By.xpath(".//button[contains(text(),\"View Fares\")]");
    private By flightName = By.xpath(".//p[contains(@class,\"responsive-bold \") and contains(text(),\"|\")]");
    private By bookNowBtn = By.xpath(".//button[contains(text(),\"Book Now\")]");


      //--------------constructor -----------------
    public SearchResultsPage(WebDriver driver) {
        this.driver = driver;
    }

    //Verify flight is present
    
    public boolean verifyFLightsPresent() {
        List<WebElement> cards = waitForCards();
        return !cards.isEmpty();
    }

    //=========================================
    // VALIDATE SEARCHED ROUTE AND DATE
    //=========================================

    public void validateSearchedRouteAndDate() {
        List<WebElement> summary = driver.findElements(searchSummary);

        String actualFrom = summary.get(0).getText().trim();
        String actualTo =   summary.get(1).getText().trim();
        String actualDate = extractDepartureDate();

        System.out.println("From city fetching --> " + actualFrom);
        System.out.println("To city fetching --> " + actualTo);
        System.out.println("Date fetching --> " + actualDate);

        if(!actualFrom.toLowerCase().contains(HomePage.expectedFrom.toLowerCase())) {
            throw new AssertionError("From city mismatch");
        }

        if(!actualTo.toLowerCase().contains(HomePage.expectedTo.toLowerCase())) {
            throw new AssertionError("To city mismatch");
        }

        DateTimeFormatter uiFormat =
                DateTimeFormatter.ofPattern("EEE, dd MMM yyy" , Locale.ENGLISH);

        String expectedDateStr = HomePage.expectedDate.format(uiFormat);

        if(!actualDate.contains(expectedDateStr)) {
            throw new AssertionError("DATE mismatch. Expected: " + expectedDateStr + " but found: " + actualDate);
        }
        ExtentManager.getTest().pass("Validation successfull: HomePage inputs match search Results. " +
                "Route: " + HomePage.expectedFrom + " -> " + HomePage.expectedTo + ", Date: " + expectedDateStr);

    }
    //=========================================
    // Logic to click the Flight as per User fare pass
    //=========================================

    public long selectFlightEqualToPrice(long targetPrice) {
        List<WebElement> cards = waitForCards();
        List<WebElement> filters = driver.findElements(priceFilter);

        if(!filters.isEmpty()) {
            JavaScriptUtil.ScrollToElement(driver, filters.get(0));
        }

        List<Long> prices = fetchAllPrices();
        for(int i =0; i < prices.size(); i++) {

            if (prices.get(i).equals(targetPrice)) {
                WebElement card = cards.get(i);
                JavaScriptUtil.ScrollToElement(driver,card);

             //cpatur flight name
                selectedFlightName = card.findElement(flightName).getText().trim();
                WebElement clickBtn = card.findElement(viewFareBtn);
                clickBtn.click();
                System.out.println("Selected Flight is " + selectedFlightName);
                WaitUtil.waitForVisible(driver,bookNowBtn);
                ExtentManager.getTest().pass("Flight selected with price: ₹ " + targetPrice);
                return targetPrice;

            }
        }
            throw new AssertionError("No FLight found with targetprice: ₹ " + targetPrice);

    }

    public String getSelectedFlightName() {
        if(selectedFlightName == null || selectedFlightName.isEmpty()) {
            throw  new IllegalStateException("Selected flight name is not available " + "Ensure selectedFlightEqualToPrice() is called before accessing it.");
        }
        return selectedFlightName;
    }

    //================================
    // Default sorting -> Ascending (CHEAPEST FIRST)
    //================================

    public boolean isPriceSortedAscendingByDefault(){
        waitForCards();
        List<Long> prcies = fetchAllPrices();
        List<WebElement> filters = driver.findElements(priceFilter);

        if(!filters.isEmpty()) {
            JavaScriptUtil.ScrollToElement(driver, filters.get(0));
        }

        for(int i =0; i < prcies.size()-1; i++) {
            if(prcies.get(i) > prcies.get(i+1)) {
                return false;
            }

        }
        ExtentManager.getTest().pass("Default sorting Validated: Prices are in ascending order");
        return true;
    }

    //============================
    //Logic to check if Price is appearing in Descending after clicking PRICE-fILTER
    //============================

    public boolean isPriceSortedDescendingAfterPriceFilter() {
        waitForCards();
        List<WebElement> filters = driver.findElements(priceFilter);

        if(filters.isEmpty()) {
            throw new AssertionError("Price filter is Missing or Unable to found...");
        }
        filters.get(0).click();
        JavaScriptUtil.ScrollToElement(driver, filters.get(0));

        List<Long> prcies = fetchAllPrices();

        for(int i =0; i < prcies.size()-1; i++) {
            if(prcies.get(i) < prcies.get(i+1)) {
                return false;
            }
        }
        ExtentManager.getTest().pass("TEST PASSED, Price is In Descending Order after clicking Price Filter");
        return true;

    }

    //----------------helpers methods---------------------

    private String extractDepartureDate() {
        List<WebElement> elements = driver.findElements(dateFetch);

        for(WebElement el : elements) {
            String text = el.getText().trim();
            if(text.matches(".*\\d{4}")) {
                return text;
            }
        }
        throw new AssertionError("Departure date not found in search summary");
    }


    private List<WebElement> waitForCards() {
        List<WebElement> cards;

        for (int i = 0; i < 20; i++) {
            cards = driver.findElements(flightCards);
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

    private List<Long> fetchAllPrices() {
        List<WebElement> priceElements = driver.findElements(priceCards);
        if(priceElements.isEmpty()) {
            throw new AssertionError("No price elements found");
        }
        List<Long> prices = new ArrayList<>();
        for(WebElement el : priceElements) {
            String text = el.getText().trim(); // ₹ 8,158.00₹ 7,858.00
            Long price = ParsePrice(text);  //
            if(price !=null) {
                prices.add(price);
            }
        }
        if(prices.size() < 2 ) {
            throw new AssertionError("Not enough valid prices to validate sorting");
        }
        return prices;
    }

    private Long ParsePrice(String text) {
        String cleaned = text.replace("₹","").trim();
        String[] parts = cleaned.split("\\s+");

        String price = parts[parts.length-1].replace(",","").trim();

        if(price.isEmpty()) {
            return null;
        }
        double value = Double.parseDouble(price);
        return Math.round(value);
    }
}
