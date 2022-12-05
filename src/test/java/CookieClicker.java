import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CookieClicker {

    static File saveFile = new File(System.getenv("APPDATA") + "\\CookieClicker.txt");
    static String readSaveFile() throws IOException {
        return Files.readString(Paths.get(saveFile.getPath()), StandardCharsets.UTF_8);
    }
    void writeSaveFile(String saveToken) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile));
        writer.write(saveToken);

        writer.close();
    }

    public static WebDriver driver;
    @BeforeAll
    static void loadCookieClicker() throws IOException {

        if(!saveFile.exists()) {
            saveFile.createNewFile();
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
        String saveToken = readSaveFile();

        driver.findElement(By.id("prefsButton")).click();
        driver.findElement(By.linkText("Import save")).click();
        driver.findElement(By.id("textareaPrompt")).sendKeys(saveToken);
        driver.findElement(By.id("promptOption0")).click();
        driver.findElement(By.id("prefsButton")).click();
    }

    @AfterEach
    void saveProgress() throws IOException {
        driver.findElement(By.id("prefsButton")).click();
        driver.findElement(By.linkText("Save")).click();
        driver.findElement(By.linkText("Export save")).click();
        writeSaveFile(driver.findElement(By.id("textareaPrompt")).getText());
        driver.close();
    }

    @Test
    void clickOnCookie() {

        long finish = System.currentTimeMillis() + 1800000; // end time
        while (System.currentTimeMillis() < finish) {
            driver.findElement(By.id("bigCookie")).click();
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
