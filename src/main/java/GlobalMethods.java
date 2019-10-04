import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Locale;

public class GlobalMethods {

    private static Actions actions;
    private GlobalMethods(){


    };

    private static WebDriver driver;
    private static  WebDriverWait wait;

    private static class SingletonHolder{
        static final GlobalMethods singleton=new GlobalMethods();
    }

    public static GlobalMethods getInstance(){
        return SingletonHolder.singleton;
    }

    public void setUp(WebDriver driver,WebDriverWait wait){
        this.driver=driver;
        this.wait=wait;
        actions = new Actions(driver);

    }

    public  void waitUntilWebElementAttributeEquals( WebElement element, String attribute, String expectedValue){


       // WebDriverWait wait=new WebDriverWait(driver,5);

        ExpectedCondition<Boolean> elementsAttributeIsEqual = arg0 -> element.getAttribute(attribute).equals(expectedValue);

        wait.until(elementsAttributeIsEqual);

    }

    public  void waitUntilElementVisible( WebElement element){

        //WebDriverWait wait = new WebDriverWait(driver, waitTime);
        wait.until(ExpectedConditions.visibilityOf(element));

    }

    public  void waitUntilClicable(WebElement element){

        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public  String getTextOfElement( WebElement element){

        waitUntilElementVisible(element);

        return element.getText();

    }

    public  void insertData(String data, WebElement input){

        input.clear();
        input.sendKeys(data);
        GlobalMethods.getInstance().waitUntilWebElementAttributeEquals( input, "value", data);

    }

    public void scrollToElement(WebElement element){

        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element);


    }

    public  void clickElement( WebElement element){

       // WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();
    }

    public  void bringCursosOverElement(WebElement element){
        actions.moveToElement(element).build().perform();

    }

    public double addStrings(String a, String b){
        double aa=Double.parseDouble(a);
        double bb=Double.parseDouble(b);

        return aa+bb;
    }

    public void waitUntilVisiible(WebElement element){
        wait.until(ExpectedConditions.invisibilityOf(element));

    }


    public void waitUntilWebElementHTMLStopUpdating(WebElement element) throws InterruptedException {

        int a=0;
        String page1 = (String)((JavascriptExecutor)driver).executeScript("return arguments[0].innerHTML;", element);
        Thread.sleep(50);
        String page2=(String)((JavascriptExecutor)driver).executeScript("return arguments[0].innerHTML;", element);

        while(!page1.equals(page2)){
            a++;
            page1 = (String)((JavascriptExecutor)driver).executeScript("return arguments[0].innerHTML;", element);

            Thread.sleep(50);
            page2=(String)((JavascriptExecutor)driver).executeScript("return arguments[0].innerHTML;", element);
        }

    }

}
