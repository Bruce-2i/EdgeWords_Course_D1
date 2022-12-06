import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.openqa.selenium.devtools.v105.page.Page.navigate;

public class Confidence {
    WebDriver webDriver;


    @BeforeAll
    void Startup()
    {
        webDriver = new ChromeDriver();
        WebDriver.Navigation navigate = webDriver.navigate();
        navigate.to("https://www.edgewordstraining.co.uk/demo-site/my-account/");
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(2));

        wait.until(ExpectedConditions.elementToBeClickable(webDriver.findElement(By.linkText("Register"))));

        webDriver.findElement(By.id("username")).sendKeys("Bruce.Devlin@2itesting.com");
        webDriver.findElement(By.id("password")).sendKeys("ThisIsAPassword");








        String wp_productCSS = "[class='product type-product post-27 status-publish first instock product_cat-accessories has-post-thumbnail sale shipping-taxable purchasable product-type-simple']";

        List<WebElement> products = webDriver.findElements(By.cssSelector(wp_productCSS));

        for (WebElement product:products) {
            product.click();

        }
    }

    /**
     * <p>Checks a discount code on an item </p>
     */
    @Test
    void CheckDiscountCode()
    {
        double obtained = 15;
        double total = 100;
        double discount = obtained * 100 / total;
        String foo = "bar";
    }
}
