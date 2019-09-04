package org.javamagazine.arquillianexample.ui;

import static org.jboss.arquillian.graphene.Graphene.guardHttp;
import org.jboss.arquillian.graphene.page.Location;
import org.junit.Assert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Abstraction of customer/Create.xhtml element.
 *
 * @author Juneau
 */
@Location("customer/Create.xhtml")
public class CreatePage {

    // Define the page logic
    @FindBy(id = "message")
    private WebElement facesMessage;

    @FindBy(id = "customerId")
    private WebElement customerId;

    @FindBy(id = "name")
    private WebElement name;

    @FindBy(id = "addressline1")
    private WebElement addressLine1;

    @FindBy(id = "addressline2")
    private WebElement addressLine2;

    @FindBy(id = "city")
    private WebElement city;

    @FindBy(id = "state")
    private WebElement state;
    
    @FindBy(id = "zip")
    private WebElement zip;

    @FindBy(id = "phone")
    private WebElement phone;

    @FindBy(id = "fax")
    private WebElement fax;

    @FindBy(id = "email")
    private WebElement email;

    @FindBy(id = "creditLimit")
    private WebElement creditLimit;

    @FindBy(id = "poolId")
    private WebElement poolId;
    
    @FindBy(id = "discountCode")
    private WebElement discountCode;

    @FindBy(id = "createAction")
    private WebElement createAction;

    public void create_customer_test() {
        customerId.sendKeys("101");
        name.sendKeys("Joe Tester");
        addressLine1.sendKeys("123 Duke Street");
        addressLine2.sendKeys("");
        city.sendKeys("San Frantonio");
        state.sendKeys("IL");
        zip.sendKeys("95035");
        phone.sendKeys("555-1212");
        fax.sendKeys("?");
        email.sendKeys("tester@joe.com");
        creditLimit.sendKeys("10000");
        poolId.sendKeys("1");
        discountCode.sendKeys("N");

        guardHttp(createAction).click();

        Assert.assertNotNull(facesMessage);
        if (facesMessage != null) {
            Assert.assertTrue("Customer Successfully Created",
                    facesMessage.getText().contains("Successfully"));
        }
    }
}
