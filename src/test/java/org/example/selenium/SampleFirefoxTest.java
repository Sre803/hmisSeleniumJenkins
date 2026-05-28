package org.example.selenium;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertNotNull;

public class SampleFirefoxTest extends BaseTest {

    @Test
    public void testExamplePageLoads() {
        driver.get("https://example.com");

        String title = driver.getTitle();
        assertNotNull("Page title should not be null", title);

        WebElement heading = driver.findElement(By.tagName("h1"));
        assertNotNull("Page should contain an h1 heading element", heading);
    }
}
