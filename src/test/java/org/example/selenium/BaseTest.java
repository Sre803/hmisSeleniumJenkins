package org.example.selenium;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;

import java.io.File;
import java.util.HashMap;

public abstract class BaseTest {

    protected WebDriver driver;
    protected JavascriptExecutor js;
    protected HashMap<String, Object> vars;

    @Before
    public void setUp() {
        String browser = System.getProperty("browser", "firefox");
        String driversLoc = System.getenv("DRIVERS_LOC");

        if (driversLoc == null || driversLoc.isEmpty()) {
            throw new IllegalStateException(
                "DRIVERS_LOC environment variable is not set. " +
                "Point it to the directory containing geckodriver and chromedriver."
            );
        }

        if (browser.equalsIgnoreCase("chrome")) {
            File chromeDriverFile = new File(driversLoc + "/chromedriver");

            ChromeDriverService service = new ChromeDriverService.Builder()
                    .usingDriverExecutable(chromeDriverFile)
                    .usingAnyFreePort()
                    .build();

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");

            driver = new ChromeDriver(service, options);

        } else {
            File geckoDriverFile = new File(driversLoc + "/geckodriver");

            GeckoDriverService service = new GeckoDriverService.Builder()
                    .usingDriverExecutable(geckoDriverFile)
                    .usingAnyFreePort()
                    .build();

            FirefoxOptions options = new FirefoxOptions();
            options.addArguments("--headless");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");

            driver = new FirefoxDriver(service, options);
        }

        js   = (JavascriptExecutor) driver;
        vars = new HashMap<>();
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
