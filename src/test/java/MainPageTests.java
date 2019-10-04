import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

public class MainPageTests extends BaseTest {


    @Test(priority = 1)
    public void testEmptyCart(){
        mainPage.verifyCartIsEmpty();
    }

    @Test(priority = 2)
    public void testAddingToCart(){
        mainPage.addCoupleRandomPopularToCart().
                unravelCart().
                verifyRightQuantityNumberOnCartsStripeDisplayed().
                verifyRightNumberOfProductsShowUp().
                verifyAddedProductsInCart().
                verifyFinalSumInCart();
    }

    @Test(priority = 3)
    public void testRemovingFromCart() throws InterruptedException {

         mainPage.unravelCart();
         mainPage.removeFromCart(0);

         //have cart updated correctly
         mainPage.verifyRightQuantityNumberOnCartsStripeDisplayed().
                 verifyRightNumberOfProductsShowUp().
                 verifyAddedProductsInCart().
                 verifyFinalSumInCart();

    }




}
