package com.happyfares.automation.utils;

import com.happyfares.automation.reporting.ExtentManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class HappyFaresCalendar {

    private final WebDriver driver;

    // ===== Locators =====
    private final By caption = By.xpath("(//span[@id='spnMonth'])[1]");
    private final By nextBtn = By.xpath("(//span[@class='right'])[1]");
    private final By prevBtn = By.xpath("(//span[@class='left'])[1]");

    public HappyFaresCalendar(WebDriver driver) {
        this.driver = driver;
    }

    // ------------------------------------------------------------
    // Read visible LEFT month (fresh DOM every time)
    // ------------------------------------------------------------
    private YearMonth readMonthYear(By calendarRoot) {

        WebElement root = WaitUtil.waitForVisible(driver, calendarRoot);
        String raw = root.findElement(caption).getText().trim();

        String txt = raw.replace("-", "")
                .replaceAll("\\s+", " ")
                .trim();

        DateTimeFormatter[] formats = new DateTimeFormatter[]{
                DateTimeFormatter.ofPattern("MMMM uuuu", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("MMM uuuu", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("uuuu MMMM", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("uuuu MMM", Locale.ENGLISH)
        };

        for (DateTimeFormatter f : formats) {
            try {
                return YearMonth.from(f.parse(txt));
            } catch (Exception ignored) {
            }
        }

        throw new IllegalStateException("Unable to parse calendar caption: " + raw);
    }

    // ------------------------------------------------------------
    // FINAL calendar pick logic (fully robust)
    // ------------------------------------------------------------
    public void pick(By calendarRoot, LocalDate targetDate) {

        if (targetDate.isBefore(LocalDate.now())) {
            throw new IllegalStateException(
                    "Cannot select past date: " + targetDate +
                            ". HappyFares calendar disables past dates."
            );
        }

        YearMonth targetYM = YearMonth.from(targetDate);
        boolean targetOnRight = false;

        // -------- NAVIGATION ONLY --------
        while (true) {

            YearMonth leftMonth = readMonthYear(calendarRoot);

            if (leftMonth.equals(targetYM)) {
                targetOnRight = false;
                break;
            }

            if (leftMonth.plusMonths(1).equals(targetYM)) {
                targetOnRight = true;
                break;
            }

            WebElement root = WaitUtil.waitForVisible(driver, calendarRoot);

            if (leftMonth.isBefore(targetYM)) {
                driver.findElement(nextBtn).click();
            }
            else {
                driver.findElement(prevBtn).click();
            }
        }

        // -------- DATE SELECTION ONLY (AFTER LOOP) --------
        String day = String.valueOf(targetDate.getDayOfMonth()); // 2025-12-7
        String calendarIndex = targetOnRight ? "2" : "1";

        By exactDay = By.xpath(
                "(//table[contains(@class,'tblCalendar')])[" + calendarIndex + "]//td[contains(@class,'emp_Cells')][.//span[text()='" + day + "']]"
        );
        //(//table[contains(@class,'tblCalendar')])[2]//td[contains(@class,'emp_Cells')][.//span[text()='7']]

        WaitUtil.waitForClickable(driver, exactDay).click();

        ExtentManager.getTest().pass("Successfully selected travel date: " + targetDate);
    }

    // how you will write dynamic xpath
}
