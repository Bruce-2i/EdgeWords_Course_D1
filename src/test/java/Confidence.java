import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.openqa.selenium.devtools.v105.page.Page.navigate;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import static java.lang.Double.parseDouble;

public class Confidence {
    static WebDriver webDriver;
    static WebDriverWait wait;
    static WebDriver.Navigation navigate;
    static List<String> orderIDs;
    static ArrayList<String> tabs;
    static NumberFormat nFormat = new DecimalFormat("#00.00");

    static void Log(String txtToLog) {
        String timeStamp = new SimpleDateFormat("dd_MM_yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
        System.out.println("[" + timeStamp + "] Tester: " + txtToLog);
    }

    @BeforeAll
    static void Setup() {
        Log("Tester: Starting WebDriver...");
        webDriver = new ChromeDriver();
        navigate = webDriver.navigate();
        Log("WebDriver alive.");

        Log("Logging into Bruce.Devlin@2itesting.com account...");
        navigate.to("https://www.edgewordstraining.co.uk/demo-site/my-account/");
        wait = new WebDriverWait(webDriver, Duration.ofSeconds(2));

        Log("Dismissing Alert...");
        webDriver.findElement(By.linkText("Dismiss")).click();
        Log("Alert dismissed.");

        webDriver.findElement(By.id("username")).sendKeys("Bruce.Devlin@2itesting.com");
        webDriver.findElement(By.id("password")).sendKeys("ThisIsAPassword");


        webDriver.findElement(By.name("login")).click();
        Log("Logged into account.");
    }

    /**
     * <p>Checks a discount code on an item </p>
     */
    @Test
    void CheckDiscountCodes() throws Exception {
        Log("Beginning Test...");
        navigate.to("https://www.edgewordstraining.co.uk/demo-site/shop/");

        Log("Getting List of Products...");
        String wp_productCSS = "[class='woocommerce-LoopProduct-link woocommerce-loop-product__link']";
        List<WebElement> products = webDriver.findElements(By.cssSelector(wp_productCSS));
        Log("Got List of Products.");

        Log("Opening each Product in a new Tab...");
        String selectLinkOpeninNewTab = Keys.chord(Keys.CONTROL,Keys.RETURN);

        for (WebElement product:products) {
            product.sendKeys(selectLinkOpeninNewTab);
        }
        tabs = new ArrayList<String>(webDriver.getWindowHandles());
        Log("Opened " + products.stream().count() + " Tabs for Products in the List.");

        Log("Creating Order Number List...");
        orderIDs = new ArrayList<String>();
        Log("Created empty Order Number List.");

        Log("Executing Test on each Product Tab...");
        for(int i = 1; i < tabs.stream().count(); ++i) {
            Log("Setting up DriverWait...");
            wait = new WebDriverWait(webDriver, Duration.ofMillis(5000));
            Log("DriverWait ready.");

            Log("Switching to current Product Tab...");
            webDriver.switchTo().window(tabs.get(i));
            wait.until(ExpectedConditions.elementToBeClickable(webDriver.findElement(By.cssSelector("[class='single_add_to_cart_button button alt']"))));
            Log("Switched to current Product Tab.");

            Log("Adding Product to the Cart...");
            webDriver.findElement(By.name("add-to-cart")).click();
            wait.until(ExpectedConditions.visibilityOf(webDriver.findElement(By.cssSelector("[class='woocommerce-message']"))));
            Log("Product added to the Cart.");

            Log("Opening the Cart...");
            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[class='cart-contents']")));
            webDriver.findElement(By.cssSelector("[class='cart-contents']")).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.name("apply_coupon")));
            Log("Cart open.");

            Log("Checking if Discount is applied...");
            List<WebElement> elements = webDriver.findElements(By.cssSelector(".cart-discount > th"));
            if (elements.size() == 0) {
                webDriver.findElement(By.id("coupon_code")).sendKeys("edgewords");
                webDriver.findElement(By.name("apply_coupon")).click();
            }
            wait.until(ExpectedConditions.textToBe(By.cssSelector(".cart-discount > th"), "Coupon: edgewords"));
            Log("Discount applied.");

            Log("Preparing Discount calculation...");
            double discountAmount, amount, markedPrice, sum;

            markedPrice = parseDouble(webDriver.findElement(By.cssSelector("[class='cart-subtotal']")).getText().replace("Subtotal £",""));
            discountAmount = 15;
            Log("Ready to calculate.");

            Log("Doing calculation...");
            sum = 100-discountAmount;
            amount = (sum*markedPrice)/100;
            double total = parseDouble(webDriver.findElement(By.cssSelector("[class='order-total']")).getText().replace("Total £",""))
                    - parseDouble(webDriver.findElement(By.id("shipping_method")).getText().replace("Flat rate: £", ""));
            Log("Calculation complete.");

            Log("Checking if Discount is correct for order...");
            if (parseDouble(nFormat.format(amount)) != parseDouble(nFormat.format(total))) {
                throw new Exception("DISCOUNT WAS WRONG ON ITEM \"" + webDriver.findElement(By.cssSelector(".product-name")).getText() +  "\"! - Subtotal: " + markedPrice + " Discount: " + discountAmount + " Expected Total: " + amount + " Actual Total: " + total);
            }
            Log("Discount is correct.");

            Log("Checking-out order...");
            navigate.to("https://www.edgewordstraining.co.uk/demo-site/checkout/");

            Log("Filling check-out details (if required)...");
            wait.until(ExpectedConditions.elementToBeClickable(By.id("billing_first_name")));
            if (webDriver.findElement(By.id("billing_first_name")).getAttribute("value") == "") webDriver.findElement(By.id("billing_first_name")).sendKeys("Bruce");
            if (webDriver.findElement(By.id("billing_last_name")).getAttribute("value") == "") webDriver.findElement(By.id("billing_last_name")).sendKeys("Devlin");
            if (webDriver.findElement(By.id("billing_address_1")).getAttribute("value") == "") webDriver.findElement(By.id("billing_address_1")).sendKeys("123, Ace Street");
            if (webDriver.findElement(By.id("billing_city")).getAttribute("value") == "") webDriver.findElement(By.id("billing_city")).sendKeys("Glasgow");
            if (webDriver.findElement(By.id("billing_postcode")).getAttribute("value") == "") webDriver.findElement(By.id("billing_postcode")).sendKeys("G40 3FQ");
            if (webDriver.findElement(By.id("billing_phone")).getAttribute("value") == "") webDriver.findElement(By.id("billing_phone")).sendKeys("1234567890");
            Log("Check-out details complete.");

            webDriver.findElement(By.id("place_order")).submit();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[class='woocommerce-order-overview__order order']")));
            Log("Checked-out order.");

            Log("Getting Order Number...");
            String orderNum = webDriver.findElement(By.cssSelector("[class='woocommerce-order-overview__order order']")).getText().replace("ORDER NUMBER:\n", "");

            Log("Adding Order Number (" + orderNum + ") to Order Number List...");
            orderIDs.add(orderNum);
            Log("Order Number added to List.");
        }
        Log("Done executing Test on all Product Tabs.");
        Log("Test Complete.");
    }
    @AfterAll
    static void completedItMate() throws Exception {
        webDriver.findElement(By.linkText("My account")).click();
        webDriver.findElement(By.linkText("Orders")).click();
        for (String orderID:orderIDs) {
            Log("Confirming Order (" + orderID + ")...");
            try {
                webDriver.findElement(By.linkText("#" + orderID)).click();
                Log("Found Order!");

                webDriver.findElement(By.linkText("Orders")).click();
            } catch (Exception ex1) {
                Log("Failed to find Order on first page, trying the next one...");
                try {
                    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, \'https://www.edgewordstraining.co.uk/demo-site/my-account/orders/2/\')]")));
                    webDriver.findElement(By.xpath("//a[contains(@href, \'https://www.edgewordstraining.co.uk/demo-site/my-account/orders/2/\')]")).click();
                    wait.until(ExpectedConditions.textToBe(By.cssSelector(".entry-title"), "Orders (page 2)"));

                    webDriver.findElement(By.linkText("#" + orderID)).click();
                    Log("Found Order!");

                    webDriver.findElement(By.linkText("Orders")).click();
                } catch (Exception ex2) {
                    throw new Exception("Unable to find order #" + orderID + "\n " + ex2.getMessage());
                }
            }
        }
        Log("Hope you enjoy, now I sleep.");
        for (String tab:webDriver.getWindowHandles()) {
            webDriver.switchTo().window(tab);
            webDriver.close();
        }
    }
}
