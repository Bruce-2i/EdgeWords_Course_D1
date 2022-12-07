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

class Product
{
    public String name = "";
    public String url = "";
}

class TestException extends Exception {
    public TestException() {
        super("Please provide a message to throw the Exception with: ");
    }

    public TestException(String message) {
        super(message);
        Confidence.TestFailed();
    }
}

public class Confidence {
    static WebDriver webDriver;
    static WebDriverWait wait;
    static WebDriver.Navigation navigate;
    static List<String> orderIDs;
    static ArrayList<String> tabs;
    static NumberFormat nFormat = new DecimalFormat("#00.00");
    static List<Product> products;
    private static long startTime;
    private static long runTime() {
        long totalTime = System.nanoTime() - startTime;
        return totalTime;
    }
    private static boolean logStampVisible = true;
    private static boolean logError = false;
    private static void log(String txtToLog) {
        String color = "\u001B[0m";
        final String stampColor = "\u001B[34m";
        String timeStamp = stampColor + "[" + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime()) + " (rt: " + Duration.ofNanos(runTime()).getSeconds() + "sec/s)]";
        if (logStampVisible)
        {
            if (logError) color = "\u001B[31m";
            System.out.println(timeStamp + "Tester: " + color + txtToLog);

        }
        else System.out.println(txtToLog);
    }

    protected static void TestFailed() {
        logError = true;
        log("Test Failed !!!");
        log("Stopping Tests...");

        log("Tests stopped.");
    }

    @BeforeAll
    static void Setup() {
        log("Tester: Starting WebDriver...");
        logStampVisible = false;
        log("--------------------------[START OF WEBDRIVER LOG]--------------------------");
        webDriver = new ChromeDriver();
        navigate = webDriver.navigate();
        log("--------------------------[END OF WEBDRIVER LOG]--------------------------");
        logStampVisible = true;
        log("WebDriver alive.");

        startTime = System.nanoTime();

        log("Logging into Bruce.Devlin@2itesting.com account...");
        navigate.to("https://www.edgewordstraining.co.uk/demo-site/my-account/");
        wait = new WebDriverWait(webDriver, Duration.ofSeconds(2));

        log("Dismissing Alert...");
        webDriver.findElement(By.linkText("Dismiss")).click();
        log("Alert dismissed.");

        webDriver.findElement(By.id("username")).sendKeys("Bruce.Devlin@2itesting.com");
        webDriver.findElement(By.id("password")).sendKeys("ThisIsAPassword");


        webDriver.findElement(By.name("login")).click();
        log("Logged into account.");
    }

    /**
     * <p>Checks a discount code on an item </p>
     */
    @Test
    void CheckDiscountCodes() throws Exception {
        log("Beginning Test...");
        navigate.to("https://www.edgewordstraining.co.uk/demo-site/shop/");

        log("Getting List of Products...");
        String wp_productCSS = "[class='woocommerce-LoopProduct-link woocommerce-loop-product__link']";
        List<WebElement> webProducts = webDriver.findElements(By.cssSelector(wp_productCSS));
        products = new ArrayList<>();
        log("Got List of Products.");

        log("Opening each Product in a new Tab...");
        String selectLinkOpeninNewTab = Keys.chord(Keys.CONTROL,Keys.RETURN);

        for (WebElement webProduct:webProducts) {
            Product product = new Product();

            product.name = webProduct.findElement(By.cssSelector(".woocommerce-loop-product__title")).getText();
            product.url = webProduct.getAttribute("href");
            products.add(product);
        }

        log("Creating Order Number List...");
        orderIDs = new ArrayList<String>();
        log("Created empty Order Number List.");

        log("Executing Test on each Product...");
        for (Product product:products) {
            log("Setting up DriverWait...");
            wait = new WebDriverWait(webDriver, Duration.ofMillis(5000));
            log("DriverWait ready.");

            log("Switching to current Product...");
            navigate.to(product.url);
            wait.until(ExpectedConditions.elementToBeClickable(webDriver.findElement(By.cssSelector("[class='single_add_to_cart_button button alt']"))));
            log("Switched to current Product.");

            log("Adding Product to the Cart...");
            webDriver.findElement(By.name("add-to-cart")).click();
            wait.until(ExpectedConditions.visibilityOf(webDriver.findElement(By.cssSelector("[class='woocommerce-message']"))));
            log("Product added to the Cart.");

            log("Opening the Cart...");
            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[class='cart-contents']")));
            webDriver.findElement(By.cssSelector("[class='cart-contents']")).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.name("apply_coupon")));
            log("Cart open.");

            log("Checking if Discount is applied...");
            List<WebElement> elements = webDriver.findElements(By.cssSelector(".cart-discount > th"));
            if (elements.size() == 0) {
                webDriver.findElement(By.id("coupon_code")).sendKeys("edgewords");
                webDriver.findElement(By.name("apply_coupon")).click();
            }
            wait.until(ExpectedConditions.textToBe(By.cssSelector(".cart-discount > th"), "Coupon: edgewords"));
            log("Discount applied.");

            log("Preparing Discount calculation...");
            double discountAmount, amount, markedPrice, sum;

            markedPrice = parseDouble(webDriver.findElement(By.cssSelector("[class='cart-subtotal']")).getText().replace("Subtotal £",""));
            discountAmount = 15;
            log("Ready to calculate.");

            log("Doing calculation...");
            sum = 100-discountAmount;
            amount = (sum*markedPrice)/100;
            double total = parseDouble(webDriver.findElement(By.cssSelector("[class='order-total']")).getText().replace("Total £",""))
                    - parseDouble(webDriver.findElement(By.id("shipping_method")).getText().replace("Flat rate: £", ""));
            log("Calculation complete.");

            log("Checking if Discount is correct for order...");
            if (parseDouble(nFormat.format(amount)) != parseDouble(nFormat.format(total))) {
                throw new TestException("DISCOUNT WAS WRONG ON ITEM \"" + webDriver.findElement(By.cssSelector(".woocommerce-cart-form__cart-item > .product-name")).getText() +  "\"! - Subtotal: " + markedPrice + " | Discount: " + discountAmount + " | Expected Total: " + amount + " | Actual Total: " + total);
            }
            log("Discount is correct.");

            log("Checking-out order...");
            navigate.to("https://www.edgewordstraining.co.uk/demo-site/checkout/");

            log("Filling check-out details (if required)...");
            wait.until(ExpectedConditions.elementToBeClickable(By.id("billing_first_name")));
            if (webDriver.findElement(By.id("billing_first_name")).getAttribute("value") == "") webDriver.findElement(By.id("billing_first_name")).sendKeys("Bruce");
            if (webDriver.findElement(By.id("billing_last_name")).getAttribute("value") == "") webDriver.findElement(By.id("billing_last_name")).sendKeys("Devlin");
            if (webDriver.findElement(By.id("billing_address_1")).getAttribute("value") == "") webDriver.findElement(By.id("billing_address_1")).sendKeys("123, Ace Street");
            if (webDriver.findElement(By.id("billing_city")).getAttribute("value") == "") webDriver.findElement(By.id("billing_city")).sendKeys("Glasgow");
            if (webDriver.findElement(By.id("billing_postcode")).getAttribute("value") == "") webDriver.findElement(By.id("billing_postcode")).sendKeys("G40 3FQ");
            if (webDriver.findElement(By.id("billing_phone")).getAttribute("value") == "") webDriver.findElement(By.id("billing_phone")).sendKeys("1234567890");
            log("Check-out details complete.");

            webDriver.findElement(By.id("place_order")).submit();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[class='woocommerce-order-overview__order order']")));
            log("Checked-out order.");

            log("Getting Order Number...");
            String orderNum = webDriver.findElement(By.cssSelector("[class='woocommerce-order-overview__order order']")).getText().replace("ORDER NUMBER:\n", "");

            log("Adding Order Number (" + orderNum + ") to Order Number List...");
            orderIDs.add(orderNum);
            log("Order Number added to List.");
        }
        log("Done executing Test on all Product Tabs.");
        log("Test Complete.");
    }
    @AfterAll
    static void completedItMate() throws Exception {
        if (orderIDs.stream().count() > 0) {
            webDriver.findElement(By.linkText("My account")).click();
            webDriver.findElement(By.linkText("Orders")).click();
            for (String orderID:orderIDs) {
                log("Confirming Order (" + orderID + ")...");
                try {
                    webDriver.findElement(By.linkText("#" + orderID)).click();
                    log("Found Order!");

                    webDriver.findElement(By.linkText("Orders")).click();
                } catch (Exception ex1) {
                    log("Failed to find Order on first page, trying the next one...");
                    try {
                        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, \'https://www.edgewordstraining.co.uk/demo-site/my-account/orders/2/\')]")));
                        webDriver.findElement(By.xpath("//a[contains(@href, \'https://www.edgewordstraining.co.uk/demo-site/my-account/orders/2/\')]")).click();
                        wait.until(ExpectedConditions.textToBe(By.cssSelector(".entry-title"), "Orders (page 2)"));

                        webDriver.findElement(By.linkText("#" + orderID)).click();
                        log("Found Order!");

                        webDriver.findElement(By.linkText("Orders")).click();
                    } catch (Exception ex2) {
                        throw new Exception("Unable to find order #" + orderID + "\n " + ex2.getMessage());
                    }
                }
                webDriver.findElement(By.linkText("My account")).click();
                webDriver.findElement(By.linkText("Logout")).click();
                wait.until(ExpectedConditions.elementToBeClickable(By.id("username")));
            }
        }
        log("Hope you enjoy, now I sleep.");
        webDriver.close();
    }
}
