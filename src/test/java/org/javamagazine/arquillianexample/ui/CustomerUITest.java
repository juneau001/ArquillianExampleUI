/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.javamagazine.arquillianexample.ui;

import java.math.BigDecimal;
import java.net.URL;
import javax.inject.Inject;
import org.javamagazine.arquillianexample.Deployments;
import org.javamagazine.arquillianexample.cdi.CustomerController;
import org.javamagazine.arquillianexample.entity.Customer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 *
 * @author Juneau
 */
@RunWith(Arquillian.class)
public class CustomerUITest {
    
    @Deployment(testable=true)
    public static WebArchive createDeployment() {
        return Deployments.getTestDeployment();
    }
    
    @Drone
    private WebDriver browser;
    
    @ArquillianResource
    private URL deploymentUrl;
    
    @Page
    CreatePage createPage;
    
    @Page
    EditPage editPage;
    
    @Inject
    CustomerController customerController;
     
    @Test
    @InSequence(1)
    @RunAsClient
    public void create_customer_test(){
        browser.get(deploymentUrl.toExternalForm() + "faces/Create.xhtml");
        createPage.create_customer_test();
        
    }
    
    
    
    //@Test
    @InSequence(2)
    @RunAsClient
    public void edit_customer_test(){
        browser.get(deploymentUrl.toExternalForm() + "faces/List.xhtml");
        editPage.edit_customer_test();
    }
    
    // Uncomment to auto-delete the test record
    //@After
    public void delete_customer_test() {
        deleteCustomerTest(BigDecimal.valueOf(Long.valueOf("101")));
    }

    private void deleteCustomerTest(BigDecimal id) {
        
        Customer customer = customerController.getCustomer(id);
        if (customer != null && customer.getCustomerId().equals(id)) {
            customerController.performDestroy(customer);
        }
        

    }
    
}
