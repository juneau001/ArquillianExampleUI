/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.javamagazine.arquillianexample.ui;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import javax.inject.Inject;
import org.javamagazine.arquillianexample.cdi.CustomerController;

import org.javamagazine.arquillianexample.cdi.util.JsfUtil;
import org.javamagazine.arquillianexample.cdi.util.PaginationHelper;

import org.javamagazine.arquillianexample.session.AbstractFacade;
import org.javamagazine.arquillianexample.entity.Customer;
import org.javamagazine.arquillianexample.session.CustomerFacade;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import static org.jboss.arquillian.graphene.Graphene.guardHttp;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 *
 * @author Juneau
 */
@RunWith(Arquillian.class)
public class CustomerCreationTest {

    private static final String WEBAPP_SRC = "src/main/webapp";
    private static final String CUSTOMER_FOLDER = "/customer";

    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "CustomerCreation.war")
                .addClasses(Customer.class, CustomerFacade.class, CustomerController.class,
                        AbstractFacade.class, JsfUtil.class, PaginationHelper.class)
                .addAsWebResource(new File(WEBAPP_SRC, "template.xhtml"))
                .addAsWebResource(new File(WEBAPP_SRC, "index.xhtml"))
                .addAsWebResource(new File(WEBAPP_SRC + "/resources/css", "cssLayout.css"))
                .addAsWebResource(new File(WEBAPP_SRC + "/resources/css", "default.css"))
                .addAsWebResource(new File(WEBAPP_SRC + "/resources/css", "jsfcrud.css"))
                .addAsWebResource(new File(WEBAPP_SRC + "/resources/js", "jsfcrud.js"))
                .addAsWebResource(new File(WEBAPP_SRC + CUSTOMER_FOLDER, "Create.xhtml"))
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource("mock-web.xml", "web.xml");
    }

    public static WebArchive createDeploymentBulk() {
        return ShrinkWrap.create(WebArchive.class, "create.war")
                .addClasses(Customer.class, CustomerFacade.class, CustomerController.class,
                        AbstractFacade.class, JsfUtil.class, PaginationHelper.class)
                .merge(ShrinkWrap.create(GenericArchive.class).as(ExplodedImporter.class)
                        .importDirectory(WEBAPP_SRC).as(GenericArchive.class),
                        "/", Filters.include(".*\\.xhtml$"))
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(
                        new StringAsset("<faces-config version=\"2.3\"/>"),
                        "faces-config.xml");
    }

    @Drone
    private WebDriver browser;

    @ArquillianResource
    private URL deploymentUrl;

    @Inject
    CustomerController customerController;

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

    @Test
    @RunAsClient
    public void create_customer_test() {
        browser.get(deploymentUrl.toExternalForm() + "faces/Create.xhtml");

        customerId.sendKeys("100");
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

    // Uncomment to auto-delete the test record
    //@After
    public void delete_customer_test() {

        deleteCustomerTest(BigDecimal.valueOf(Long.valueOf("100")));
    }

    private void deleteCustomerTest(BigDecimal id) {

        Customer customer = customerController.getCustomer(id);
        if (customer != null && customer.getCustomerId().equals(id)) {
            customerController.performDestroy(customer);
        }
    }

}
