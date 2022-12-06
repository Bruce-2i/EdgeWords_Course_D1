import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class HelloWebDriver {
    /**<p>Variable to store the WebDriver.</p>*/
    WebDriver webDriver;

    /**<p>The WebDriver Navigation.</p>*/
    WebDriver.Navigation navigate;

    /**<p>Quit and dispose of the WebDriver after each test.</p>*/
    @AfterEach
    void webDriverCleanup() {
        webDriver.quit();
        webDriver = null;
    }

    /**<p>Setup the WebDriver before each test.</p>*/
    @BeforeEach
    void webDriverSetup() {
        //Create a new Chrome WebDriver.
        WebDriverManager.chromedriver().setup();
        webDriver = new ChromeDriver();
    }

    /**
     * <p>Login to an authenticated web-page using credentials.</p>
     * @throws Exception The user details provided where refused by the web-page.
     */
    @Test
    void loginToAuthPage() throws Exception {
        //Set up the variables used in the test.
        String url = "https://www.edgewordstraining.co.uk/webdriver2/" ;
        String username = "edgewords";
        String password = "edgewords123";

        //Navigate to page.
        navigate = webDriver.navigate();
        navigate.to(url);

        //Click login button.
        webDriver.findElement(By.linkText("Login To Restricted Area")).click();

        //Enter Login details
        webDriver.findElement(By.id("username")).sendKeys(username);
        webDriver.findElement(By.id("password")).sendKeys(password);

        //Submit those details.
        webDriver.findElement(By.linkText("Submit")).click();

        //Instantiate the detailsAccepted bool.
        boolean detailsAccepted = true;

        //w.Until(ExpectedConditions.alertIsPresent()) creates an exception when it times-out...
        //... searching for an alert.
        //It's best to suppress this exception as we won't always see the exception.
        try
        {
            //Wait 2 seconds for page to verify details.
            WebDriverWait w = new WebDriverWait(webDriver, Duration.ofSeconds(2));

            //If there is an alert present...
            if(w.until(ExpectedConditions.alertIsPresent()) != null)
            {
                //... we mark the details as "accepted = false".
                detailsAccepted = false;
            }
        }
        catch (Exception exception)
        {
            /*
            I must put a catch here, idk what to do with it...
            So here is some ASCII art I guess <3
            ___██__________██
            ___█▒█________█▒█
            __█▒███____███▒█
            __█▒████████▒▒█
            __█▒████▒▒█▒▒██
            __████▒▒▒▒▒████
            ___█▒▒▒▒▒▒▒████
            __█▒▒▒▒▒▒▒▒████______█
            _██▒█▒▒▒▒▒█▒▒████__█▒█
            _█▒█●█▒▒▒█●█▒▒███_█▒▒█
            _█▒▒█▒▒▒▒▒█▒▒▒██_█▒▒█
            __█▒▒▒=▲=▒▒▒▒███_██▒█
            __██▒▒█♥█▒▒▒▒███__██▒█
            ____███▒▒▒▒████____█▒█
            ______██████________███
            _______█▒▒████______██
            _______█▒▒▒▒▒████__██
            _______█▒██▒██████▒█
            _______█▒███▒▒▒█████
            _____█▒████▒▒▒▒████
            ______█▒███▒██████__
            */
        }

        //Check if the details where refused.
        if (!detailsAccepted)
        {
            //Fail the test if the detail are incorrect.
            throw new Exception("Failed to Login using details provided.");
        }
        else
        {
            //Wait until the user is logged in.
            WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(2));
            wait.until(ExpectedConditions.elementToBeClickable(webDriver.findElement(By.linkText("Log Out"))));

            //Logout.
            webDriver.findElement(By.linkText("Log Out")).click();

            //Wait until alert is present.
            if(wait.until(ExpectedConditions.alertIsPresent()) != null) {
                webDriver.switchTo().alert().accept();
            }
        }
    }

    /**<p>Drags a slider on a Web-page.</p>*/
    @Test
    void dragSlider() {
        //Navigate to page.
        navigate = webDriver.navigate();
        navigate.to("https://www.edgewordstraining.co.uk/webdriver2/docs/cssXPath.html");

        //Create Gripper.
        WebElement gripper = webDriver.findElement(By.cssSelector(".ui-slider-handle"));

        //Create the Action.
        Actions actions = new Actions(webDriver);
        Action dragDrop = actions.moveToElement(gripper)
                .clickAndHold().moveByOffset(100, 0)
                .release().build();
        //Execute Action.
        dragDrop.perform();
    }
}
