
package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CrossBrowserTest {

    private static final String BASE_URL =
            System.getProperty("baseUrl", "http://localhost:8000");

    private static final boolean HEADLESS =
            Boolean.parseBoolean(System.getProperty("headless", "false"));

    @DataProvider(name = "browsers")
    public Object[][] browsers() {
        return new Object[][]{
                {"chrome"},
                {"firefox"}
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
                    driver.findElement(By.id("login-form")).isDisplayed(),
                    "Login form should be visible"
            );

            System.out.println(
                    browser + ": Homepage test passed"
            );

        } finally {
            driver.quit();
        }
    }

    @Test(dataProvider = "browsers")
    public void verifyValidLogin(String browser) {
        WebDriver driver = createDriver(browser);

        try {
            driver.get(BASE_URL);

            driver.findElement(By.id("username"))
                    .sendKeys("student");

            driver.findElement(By.id("password"))
                    .sendKeys("demo123");

            driver.findElement(By.id("login-button"))
                    .click();

            WebElement dashboard =
                    driver.findElement(By.id("dashboard"));

            Assert.assertTrue(
                    dashboard.isDisplayed(),
                    "Dashboard should appear after valid login"
            );

            Assert.assertTrue(
                    driver.findElement(By.id("welcome-message"))
                            .getText().contains("student"),
                    "Welcome message should include the username"
            );

            System.out.println(
                    browser + ": Valid login test passed"
            );

        } finally {
            driver.quit();
        }
    }

    @Test(dataProvider = "browsers")
    public void verifyInvalidLogin(String browser) {
        WebDriver driver = createDriver(browser);

        try {
            driver.get(BASE_URL);

            driver.findElement(By.id("username"))
                    .sendKeys("wronguser");

            driver.findElement(By.id("password"))
                    .sendKeys("wrongpass");

            driver.findElement(By.id("login-button"))
                    .click();

            WebElement message =
                    driver.findElement(By.id("message"));

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

            System.out.println(
                    browser + ": Invalid login test passed"
            );

        } finally {
            driver.quit();
        }
    }

    @Test(dataProvider = "browsers")
    public void verifyLogout(String browser) {
        WebDriver driver = createDriver(browser);

        try {
            driver.get(BASE_URL);

            driver.findElement(By.id("username"))
                    .sendKeys("student");

            driver.findElement(By.id("password"))
                    .sendKeys("demo123");

            driver.findElement(By.id("login-button"))
                    .click();

            Assert.assertTrue(
                    driver.findElement(By.id("dashboard"))
                            .isDisplayed(),
                    "Dashboard should appear before logout"
            );

            driver.findElement(By.id("logout-button"))
                    .click();

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

            System.out.println(
                    browser + ": Logout test passed"
            );

        } finally {
            driver.quit();
        }
    }
}
