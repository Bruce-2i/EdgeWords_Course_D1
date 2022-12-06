import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;


import static java.lang.Integer.parseInt;

public class CookieClicker {

    static int cookieCount() {
        String cookies = driver.findElement(By.id("cookies")).getText().split("\n")[0]
                .replace(" cookies","")
                .replace(",","");
        return parseInt(cookies);
    }
    static int saveInterval = 300000; //300000 = 5 MINS
    static int upgradeInterval = 120000; //120000 = 2 MINS

    static File saveFolder = new File(System.getenv("APPDATA") + "\\CookieClicker\\");
    static File saveFile(boolean active)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
        Date date = new Date();

        String saveFilePath;
        if (active) saveFilePath = saveFolder.getPath() + "\\#CookieClicker_CurrentSave.txt";
        else saveFilePath = saveFolder.getPath() + "\\CookieClickerSave_" + formatter.format(date) + ".txt";

        return new File(saveFilePath);
    }

    static boolean noSaveFile = false;

    static String readSaveFile() throws IOException {
        return Files.readString(Paths.get(saveFile(true).getPath()), StandardCharsets.UTF_8);
    }
    void writeSaveFile(String saveToken) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile(true)));
        writer.write(saveToken);

        writer.close();

        File backupSave = saveFile(false);
        FileUtils.copyFile(saveFile(true), saveFile(false));
    }

    public static WebDriver driver;
    @BeforeAll
    static void loadCookieClicker() throws IOException {

        if (!saveFolder.exists())
        {
            saveFolder.mkdir();
        }
        if (!saveFile(true).exists()) {
            saveFile(true).createNewFile();
            noSaveFile = true;
        }

        driver = new ChromeDriver();

        driver.get("https://orteil.dashnet.org/cookieclicker/");

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        driver.findElement(By.id("langSelect-EN")).click();

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void loadProgress() throws IOException {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        driver.findElement(By.id("prefsButton")).click();
        //applySettings();

        if (!noSaveFile) {
            String saveToken = readSaveFile();

            driver.findElement(By.linkText("Import save")).click();
            driver.findElement(By.id("textareaPrompt")).sendKeys(saveToken);
            driver.findElement(By.id("promptOption0")).click();
        }
    }

    void applySettings() throws  IOException {
        driver.findElement(By.id("volumeSlider")).sendKeys("16");
        driver.findElement(By.id("volumeSlider")).click();

        driver.findElement(By.linkText("fancyButton")).click();
    }

    @AfterEach
    void saveProgress() throws IOException {
        driver.findElement(By.linkText("Save")).click();
        driver.findElement(By.linkText("Export save")).click();
        writeSaveFile(driver.findElement(By.id("textareaPrompt")).getText());
        driver.findElement(By.id("promptOption0")).click();
    }

    void UpgradeAll()
    {
        List<WebElement> Products = driver.findElements(By.cssSelector("[class='product unlocked enabled']"));

        Collections.reverse(Products);

        for (WebElement product:Products) {
            int price = parseInt(product.findElement(By.cssSelector("[class='price']")).getText().replace(",",""));
            int currentCookies = cookieCount();
            int target = currentCookies / price;

            for(int i=0; i<target; i++){
                product.click();
            }
        }
    }

    @Test
    void clickOnCookie() throws IOException {
        while (true) {
            //Stop Clicking in 5 minutes.
            long saveTime = System.currentTimeMillis() + saveInterval;
            while (System.currentTimeMillis() < saveTime) {
                long upgradeTime = System.currentTimeMillis() + upgradeInterval;
                while (System.currentTimeMillis() < upgradeTime) {
                    driver.findElement(By.id("bigCookie")).click();
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                UpgradeAll();
            }
            saveProgress();
        }
    }
}
