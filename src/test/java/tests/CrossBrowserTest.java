
package tests;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CrossBrowserTest {

    private static final String BASE_URL =
            System.getProperty("baseUrl", "http://localhost:8000");

    private static final boolean HEADLESS =
            Boolean.parseBoolean(System.getProperty("headless", "false"));

    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);

    @DataProvider(name = "browsers")
    public Object[][] browsers() {
        return new Object[][] {
                { "chrome" },
                { "firefox" }
        };
    }

    private WebDriver createDriver(String browser) {
        if (browser.equalsIgnoreCase("chrome")) {
            ChromeOptions options = new ChromeOptions();

            if (HEADLESS) {
                options.addArguments("--headless=new");
            }

            return new ChromeDriver(options);

        } else if (browser.equalsIgnoreCase("firefox")) {
            FirefoxOptions options = new FirefoxOptions();

            if (HEADLESS) {
                options.addArguments("-headless");
            }

            return new FirefoxDriver(options);
        }

        throw new IllegalArgumentException(
                "Unsupported browser: " + browser
        );
    }

    private WebDriverWait createWait(WebDriver driver) {
        return new WebDriverWait(driver, WAIT_TIMEOUT);
    }

    @Test(dataProvider = "browsers")
    public void verifyHomePage(String browser) {
        WebDriver driver = createDriver(browser);

        try {
            driver.get(BASE_URL);

            Assert.assertEquals(
                    driver.getTitle(),
                    "Student Portal",
                    "Homepage title should match"
            );

            Assert.assertTrue(
                    createWait(driver).until(
                            ExpectedConditions.visibilityOfElementLocated(
                                    By.id("login-form")
                            )
                    ).isDisplayed(),
                    "Login form should be visible"
            );

            System.out.println(browser + ": Homepage test passed");

        } finally {
            driver.quit();
        }
    }

    @Test(dataProvider = "browsers")
    public void verifyValidLogin(String browser) {
        WebDriver driver = createDriver(browser);

        try {
            driver.get(BASE_URL);

            WebDriverWait wait = createWait(driver);

            wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.id("username")
                    )
            ).sendKeys("student");

            driver.findElement(By.id("password"))
                    .sendKeys("demo123");

            driver.findElement(By.id("login-button"))
                    .click();

            WebElement dashboard = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.id("dashboard")
                    )
            );

            Assert.assertTrue(
                    dashboard.isDisplayed(),
                    "Dashboard should appear after valid login"
            );

            Assert.assertTrue(
                    wait.until(
                            ExpectedConditions.visibilityOfElementLocated(
                                    By.id("welcome-message")
                            )
                    ).getText().contains("student"),
                    "Welcome message should include the username"
            );

            System.out.println(browser + ": Valid login test passed");

        } finally {
            driver.quit();
        }
    }

    @Test(dataProvider = "browsers")
    public void verifyInvalidLogin(String browser) {
        WebDriver driver = createDriver(browser);

        try {
            driver.get(BASE_URL);

            WebDriverWait wait = createWait(driver);

            wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.id("username")
                    )
            ).sendKeys("wronguser");

            driver.findElement(By.id("password"))
                    .sendKeys("wrongpass");

            driver.findElement(By.id("login-button"))
                    .click();

            WebElement message = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.id("message")
                    )
            );

            wait.until(
                    ExpectedConditions.textToBe(
                            By.id("message"),
                            "Invalid username or password."
                    )
            );

            Assert.assertEquals(
                    message.getText(),
                    "Invalid username or password.",
                    "An error should appear for invalid credentials"
            );

            Assert.assertTrue(
                    driver.findElement(By.id("login-section"))
                            .isDisplayed(),
                    "Login section should remain visible"
            );

            System.out.println(browser + ": Invalid login test passed");

        } finally {
            driver.quit();
        }
    }

    @Test(dataProvider = "browsers")
    public void verifyLogout(String browser) {
        WebDriver driver = createDriver(browser);

        try {
            driver.get(BASE_URL);

            WebDriverWait wait = createWait(driver);

            // Enter valid credentials.
            wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.id("username")
                    )
            ).sendKeys("student");

            driver.findElement(By.id("password"))
                    .sendKeys("demo123");

            driver.findElement(By.id("login-button"))
                    .click();

            // Wait until the dashboard is visible.
            wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.id("dashboard")
                    )
            );

            // Click logout.
            wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.id("logout-button")
                    )
            ).click();

            // Wait for the dashboard to become hidden.
            wait.until(
                    ExpectedConditions.invisibilityOfElementLocated(
                            By.id("dashboard")
                    )
            );

            // Verify that the login screen has returned.
            wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.id("login-section")
                    )
            );

            wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.id("username")
                    )
            );

            Assert.assertTrue(
                    driver.findElement(By.id("login-section"))
                            .isDisplayed(),
                    "Login screen should return after logout"
            );

            Assert.assertFalse(
                    driver.findElement(By.id("dashboard"))
                            .isDisplayed(),
                    "Dashboard should be hidden after logout"
            );

            System.out.println(browser + ": Logout test passed");

        } finally {
            driver.quit();
        }
    }
}
