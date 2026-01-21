package com.happyfares.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class SearchResultsPage {

    private WebDriver driver;

    //-------------Locators---------------------------
    private By flightCards = By.xpath("//div[@class='row ng-scope']");


      //--------------constructor -----------------
    public SearchResultsPage(WebDriver driver) {
        this.driver = driver;
    }

    //Verify flight is present
    
    public boolean verifyFLightsPresent() {
        List<WebElement> cards = waitForCards();
        return !cards.isEmpty();
    }

    //----------------helpers methods---------------------

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
}
