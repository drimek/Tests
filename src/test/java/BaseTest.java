import org.kohsuke.rngom.parse.host.Base;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class BaseTest {


    protected static WebDriver driver = null;
    protected static WebDriverWait wait;
    protected static final String MainPageURL="http://automationpractice.com/index.php";
    protected static MainPage mainPage;


    @BeforeClass
    public static void launch(){
        driver = new ChromeDriver();
        driver.manage().window().maximize();

        wait=new WebDriverWait(driver,30);
        GlobalMethods.getInstance().setUp(driver,wait);
        driver.get(MainPageURL);
        mainPage = new MainPage(driver,wait);
       // driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @AfterClass
    public static void closeBrowser(){
        driver.quit();
    }

}
