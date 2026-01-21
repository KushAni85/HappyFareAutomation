package com.happyfares.automation.pages;

import com.happyfares.automation.reporting.ExtentManager;
import com.happyfares.automation.utils.HappyFaresCalendar;
import com.happyfares.automation.utils.WaitUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;

public class HomePage {
    private WebDriver driver;

    private By fromcity = By.xpath("//input[contains(@placeholder,\"Select Origin City\")]");
    private By toCity = By.xpath("//input[contains(@placeholder,\"Select Destination City\")]");
    private By searchBtn = By.xpath("//input[@value = \"Search\"]");
    private By departDateInput = By.xpath("(//label[contains(@class,\"datepicker search-date\")])[1]");


    public HomePage(WebDriver driver) {
        this.driver = driver;
    }

    public void SelectFromCity(String city) {
        WaitUtil.waitForVisible(driver, fromcity);
        WebElement from = driver.findElement(fromcity);
        from.click();
        from.clear();
        from.sendKeys(city);
        from.click();
    }

    public void SelectToCity(String city) {
        WaitUtil.waitForVisible(driver, fromcity);
        WebElement to = driver.findElement(toCity);
        to.click();
        to.clear();
        to.sendKeys(city);

        WaitUtil.waitForVisible(driver, By.cssSelector("#anguScroll_dropdown .angucomplete-row"));
        to.click();
    }

    public void openDepartureCalendar() {
        WebElement departInput = WaitUtil.waitForClickable(driver, departDateInput);
        departInput.click();
    }

    public By getCalendarRoot() {
        return By.xpath("//div[contains(@class,\"dtePic-body\")]");
    }

    public void search() {
        driver.findElement(searchBtn).click();
    }

    public void searchFlight(String fromCity, String toCity, LocalDate traveldate, HappyFaresCalendar calendar) throws InterruptedException {
        ExtentManager.getTest().info("Start flight search from " + fromCity + " to " + toCity +
                " wth departure date " + traveldate);

        SelectFromCity(fromCity);
        ExtentManager.getTest().info("Selected source city " + fromCity);

        SelectToCity(toCity);
        ExtentManager.getTest().info("Selected Destination city " + toCity);


        openDepartureCalendar();
        calendar.pick(getCalendarRoot(), traveldate);

        ExtentManager.getTest().info("initiating flight search..");
        search();

    }

}
