package org.javamagazine.arquillianexample.ui;

import static org.jboss.arquillian.graphene.Graphene.guardHttp;
import static org.jboss.arquillian.graphene.Graphene.waitAjax;
import org.jboss.arquillian.graphene.page.Location;
import static org.junit.Assert.assertEquals;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Abstraction of customer/Edit.xhtml element.
 * 
 * @author Juneau
 */
@Location("customer/Edit.xhtml")
public class EditPage {
    
    
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

    @FindBy(id = "editAction")
    private WebElement editAction;
    
    public void edit_customer_test() {
        
        city.sendKeys("Chicago");
        state.sendKeys("IL");
        phone.sendKeys("555-1212");
        fax.sendKeys("?");
        email.sendKeys("tester@joe.com");
        creditLimit.sendKeys("10000");
        poolId.sendKeys("1");

        guardHttp(editAction).click();
        
        waitAjax().until().element(facesMessage).is().present();
        assertEquals("Customer Successfully Created", facesMessage.getText().trim());
    }
}
