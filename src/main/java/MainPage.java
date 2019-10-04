
import jdk.nashorn.internal.objects.Global;
import org.apache.http.util.Asserts;
import org.hamcrest.beans.HasProperty;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import java.sql.Struct;
import java.util.concurrent.TimeUnit;


public class MainPage {

    private static WebDriver driver;
    private static WebDriverWait wait;

    //for quantity of every single product
    private HashMap<String,Integer> productQuantityDictionary;
    //non-duplicated products
    private List<Product> addedProducts=new ArrayList<>();

    @FindBy(xpath = "(//a[contains(text(),'Popular')])[1]")
    private WebElement popularButton;
    @FindBy(xpath = "(//a[contains(text(),'Best Sellers')])[1]")
    private WebElement bestSellersButton;
    @FindBy(id="homefeatured")
    private WebElement productList;
    @FindBy(className = "shopping_cart")
    private WebElement wholeCart;
    @FindBy(xpath = "//a[@title='View my shopping cart']")
    private WebElement cart;
    @FindBy(id = "button_order_cart")
    private WebElement checkOut;
    @FindBy(id = "cart_block_list")
    private WebElement cartBlock;
    @FindBy(xpath = "//a[@title='Proceed to checkout']")
    private WebElement proceedToCheckoutButton;
    @FindBy(xpath = "//*[@title='Continue shopping']")
    private WebElement continueShoppingButton;
    @FindBy(xpath = "//*[contains(@class,'ajax_cart_quantity unvisible')]")
    private WebElement cartQuantity;
    @FindBy(className = "ajax_cart_no_product")
    private WebElement cartEmpty;
    @FindBy(xpath = "//div[@class='cart-prices-line last-line']/span[1]")
    private WebElement totalPrice;
    @FindBy(xpath = "//*[@class='cart-prices']/div[1]/span[1]")
    private WebElement shippingPrice;

    //used for looking for id of the product
    private String idInURL="id_product";
    private String PRODUCT_ROW_IN_CART=".//dt[contains(@data-id,'cart_block_product')]";
    private String ADD_TO_CART=".//*[@title='Add to cart']";
    private final String PRODUCT_PRICE = ".//div[@class='right-block']//div[@class='content_price']/span";


    public MainPage(WebDriver driver,WebDriverWait wait){
        this.driver=driver;
        this.wait=wait;
        PageFactory.initElements(driver, this);
        productQuantityDictionary=new HashMap<String, Integer>();

    }

    //>2 diffrent products
    public MainPage addCoupleRandomPopularToCart(){

        WebElement parent = popularButton.findElement(By.xpath(".."));
        String className=parent.getAttribute("class");

        if(!className.equals("active")){

            GlobalMethods.getInstance().clickElement(popularButton);
        }else{

            List<WebElement> products = productList.findElements(By.xpath(".//div[@class='product-container']"));
            Random random=new Random();

            // how many to add
            int sizeOfCart=ThreadLocalRandom.current().nextInt(2, products.size());

            int i=0;
            int index=0;
            //add all to cart/
            for(i=0;i<sizeOfCart;i++){
                index=random.nextInt(products.size());
                WebElement product=products.get(index);
                addToCart(product);
            }
            // add at least 2 diffrent products
            int j=(index<1)?1:0;
            WebElement product=products.get(j);
            addToCart(product);
            //be sure to add one of the product at least twice
            product=products.get(index);
            addToCart(product);

        }
        return this;
    }

    public MainPage verifyCartIsEmpty(){
        String emptyText=cartEmpty.getText();
        //is displayed
        Assert.assertEquals(cartEmpty.isDisplayed(),true);
        // should show empty
        Assert.assertEquals(emptyText,"(empty)");
        return this;
    }

    public MainPage verifyRightQuantityNumberOnCartsStripeDisplayed(){
        //sum every product*quantity
        int quantity=0;
        for(int i=0;i<addedProducts.size();i++){

            quantity+=productQuantityDictionary.get(addedProducts.get(i).id);
        }
        Assert.assertEquals(getNumberOfProductsInCart(),quantity);
        return this;
    }


    public MainPage unravelCart(){
        //unravel the cart
        GlobalMethods.getInstance().scrollToElement(cart);
        GlobalMethods.getInstance().bringCursosOverElement(cart);
        GlobalMethods.getInstance().waitUntilElementVisible(checkOut);
        return  this;
    }

    public MainPage verifyRightNumberOfProductsShowUp(){
        List<WebElement> productsInCart=wholeCart.findElements(By.xpath(PRODUCT_ROW_IN_CART));
        Assert.assertEquals(addedProducts.size(),productsInCart.size(),"Wrong number of products show up in cart");
        return  this;
    }



    public MainPage verifyAddedProductsInCart(){
        //all products in cart
        List<WebElement> productsInCart=wholeCart.findElements(By.xpath(PRODUCT_ROW_IN_CART));
        //if(productsInCart.size()==addedProducts.size()) {

            for (int i=0;i<productsInCart.size();i++) {

                WebElement productInCart=productsInCart.get(i);

                try{

                    FluentWait<WebDriver> fluentWait = new FluentWait<>(driver)
                            .withTimeout(20, TimeUnit.SECONDS)
                            .pollingEvery(2, TimeUnit.SECONDS)
                            .ignoring(NoSuchElementException.class);

                    //isproduct displayed
                    productInCart.isDisplayed();

                    String url=productInCart.findElement(By.className("cart-images")).getAttribute("href");

                    //get all attributes of product to check
                    String id=pullIdOfTheProductFromUrl(url);
                    String name=productInCart.findElement(By.xpath(".//a[@class='cart_block_product_name']")).getAttribute("title");
                    String priceInCart=productInCart.findElement(By.className("price")).getText();
                    WebElement quantityE=productInCart.findElement(By.className("quantity-formated"));

                    fluentWait.until(ExpectedConditions.visibilityOf(quantityE));

                    GlobalMethods.getInstance().waitUntilElementVisible(quantityE);
                    String quantitystr=quantityE.getText();

                    //remove " X" from quantity
                    quantitystr=quantitystr.substring(0,quantitystr.length()-3);
                    Integer quantity=Integer.parseInt(quantitystr);

                    //quantity * price
                    String fullprice="$"+getFullPrice(addedProducts.get(i).price,productQuantityDictionary.get( addedProducts.get(i).id) );

                    //Assert
                    Assert.assertEquals(addedProducts.get(i).id,id,"ID should be identical");
                    Assert.assertEquals(addedProducts.get(i).name,name,"name of the product with ID: "+addedProducts.get(i).id+" is wrong");
                    Assert.assertEquals(fullprice,priceInCart,"Price for the product with ID: "+addedProducts.get(i).id+" is wrong");
                    Assert.assertEquals(productQuantityDictionary.get(addedProducts.get(i).id),quantity,"Quantity of product with ID: "+addedProducts.get(i).id+"in cart is wrong");

                }catch (NoSuchElementException e){
                    System.out.println("Product not displayed in cart");
                }

            }

        return this;

    }

    public int getNumberOfProductsInCart(){

        return Integer.parseInt(cartQuantity.getText());
    }

    private void addToCart(WebElement product){

        //first add to cart
        WebElement addToCart=product.findElement(By.xpath(ADD_TO_CART));
        GlobalMethods.getInstance().bringCursosOverElement(product);
        GlobalMethods.getInstance().clickElement(addToCart);
        GlobalMethods.getInstance().clickElement(continueShoppingButton);

        //second add to list
        String URL=product.findElement(By.xpath(".//*[@itemprop='url']")).getAttribute("href");
        String id=pullIdOfTheProductFromUrl(URL);
        //first time product added
        if(productQuantityDictionary.get(id)==null){

            productQuantityDictionary.put(id,1);
            WebElement nameOfProduct=product.findElement(By.className("product-name"));
            String name=nameOfProduct.getText();
            String price=product.findElement(By.xpath(PRODUCT_PRICE)).getText();
            addedProducts.add(new Product(id,name,price));

            //every next time added
        }else {

            Integer a = productQuantityDictionary.get(id);
            //increase product quantity
            a++;
            productQuantityDictionary.replace(id, a);
        }
    }

    public WebElement getFromCart(int index){

        WebElement product=wholeCart.findElements(By.xpath(PRODUCT_ROW_IN_CART)).get(index);
        return product;
    }

    public MainPage removeFromCart(int index) throws InterruptedException {

        int size=wholeCart.findElements(By.xpath(PRODUCT_ROW_IN_CART)).size();
        //get product
        WebElement toRemove=getFromCart(index);

        //get id of product
        String url=toRemove.findElement(By.className("cart-images")).getAttribute("href");
        String id=pullIdOfTheProductFromUrl(url);

        WebElement removeButton=toRemove.findElement(By.className("remove_link"));
        //click remove button
        GlobalMethods.getInstance().clickElement(removeButton);
        GlobalMethods.getInstance().waitUntilVisiible(toRemove);
        GlobalMethods.getInstance().waitUntilWebElementHTMLStopUpdating(wholeCart);
        // remove from list and dictionary
        //find in list
        int i=0;
        for(i=0;i<addedProducts.size();i++){
            if(addedProducts.get(i).id.equals(id)){
                addedProducts.remove(i);
                break;
            }
        }
        productQuantityDictionary.remove(id);


        return this;
    }


    private String getShippingPrice(){

        String price=shippingPrice.getText();
        //remove $
        return  price.substring(1);
    }


    public MainPage verifyFinalSumInCart(){
        List<WebElement> productsInCart=wholeCart.findElements(By.xpath(PRODUCT_ROW_IN_CART));

        double fullSum=0;

        for(int i=0;i<productsInCart.size();i++){
            WebElement productInCart=productsInCart.get(i);
            String priceInCart=productInCart.findElement(By.className("price")).getText();
            //quantity*basic price
            String fullprice=getFullPrice(addedProducts.get(i).price,productQuantityDictionary.get( addedProducts.get(i).id) );
            fullSum+=Double.parseDouble(fullprice);
        }
        //what it should be
        fullSum += Double.parseDouble(getShippingPrice());

        Assert.assertEquals(String.format(Locale.US, "%.2f", fullSum),totalPrice.getText().substring(1),"Total sum in cart is incorrect");
        return  this;
    }


    private String getFullPrice(String productPrice, Integer quantity){

        double quantityDouble=(double)quantity;

        //remove $
        productPrice=productPrice.substring(1,productPrice.length());

        double productPricedouble=Double.parseDouble(productPrice);
        double fullPrice=quantityDouble*productPricedouble;
        return String.format(Locale.US, "%.2f", fullPrice);

    }

    private String pullIdOfTheProductFromUrl(String url){

        //where id starts
        int index=url.indexOf(idInURL) +idInURL.length()+1;

        StringBuilder builder=new StringBuilder();

        for ( int i=index;i<url.length();i++  ){
            //where it ends
            if(url.charAt(i)=='&'){
                break;
            }
            builder.append(url.charAt(i));
        }

        return builder.toString();        //String a=url.substring()

    }

    class Product{


        String id;
        String name;
        String price;

        public Product(String id,String name, String price){
            this.id=id;
            this.name=name;
            this.price=price;

        }

    }


}
