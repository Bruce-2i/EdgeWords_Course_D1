// Generated by Selenium IDE
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Keys;
import java.util.*;
import java.net.MalformedURLException;
import java.net.URL;
import static org.hamcrest.MatcherAssert.assertThat;
public class SelIDEDemo1Test {
  private WebDriver driver;
  @BeforeEach
  public void setUp() {
    driver = new ChromeDriver();
  }
  @AfterEach
  public void tearDown() {
    driver.quit();
  }
  @Test
  public void selIDEDemo1() {
    // Test name: selIDEDemo1
    // Step # | name | target | value
    // 1 | open | https://www.edgewordstraining.co.uk/webdriver2/ | 
    driver.get("https://www.edgewordstraining.co.uk/webdriver2/");
    // 2 | setWindowSize | 976x1056 | 
    driver.manage().window().setSize(new Dimension(976, 1056));
    // 3 | click | linkText=Access Basic Examples Area | 
    driver.findElement(By.linkText("Access Basic Examples Area")).click();
    // 4 | click | linkText=Forms | 
    driver.findElement(By.linkText("Forms")).click();
    // 5 | click | id=textInput | 
    driver.findElement(By.id("textInput")).click();
    // 6 | type | id=textInput | Bruce
    driver.findElement(By.id("textInput")).sendKeys("Bruce");
    // 7 | click | id=textArea | 
    driver.findElement(By.id("textArea")).click();
    // 8 | type | id=textArea | Hello World
    driver.findElement(By.id("textArea")).sendKeys("Hello World");
    // 9 | click | id=checkbox | 
    driver.findElement(By.id("checkbox")).click();
    // 10 | click | id=select | 
    driver.findElement(By.id("select")).click();
    // 11 | select | id=select | label=Selection Two
    {
      WebElement dropdown = driver.findElement(By.id("select"));
      dropdown.findElement(By.xpath("//option[. = 'Selection Two']")).click();
    }
    // 12 | click | css=.formTable tr:nth-child(5) > td:nth-child(2) | 
    driver.findElement(By.cssSelector(".formTable tr:nth-child(5) > td:nth-child(2)")).click();
    // 13 | click | id=two | 
    driver.findElement(By.id("two")).click();
    // 14 | click | id=password | 
    driver.findElement(By.id("password")).click();
    // 15 | type | id=password | 123
    driver.findElement(By.id("password")).sendKeys("123");
    // 16 | click | linkText=Submit | 
    driver.findElement(By.linkText("Submit")).click();
    // 17 | click | id=textInputValue | 
    driver.findElement(By.id("textInputValue")).click();
    // 18 | click | id=textInputValue | 
    driver.findElement(By.id("textInputValue")).click();
    // 19 | doubleClick | id=textInputValue | 
    {
      WebElement element = driver.findElement(By.id("textInputValue"));
      Actions builder = new Actions(driver);
      builder.doubleClick(element).perform();
    }
    // 20 | assertText | id=textInputValue | Bruce
    assertThat(driver.findElement(By.id("textInputValue")).getText(), is("Bruce"));
  }
}
