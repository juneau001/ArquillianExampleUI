package org.javamagazine.arquillianexample.cdi;

import org.javamagazine.arquillianexample.entity.Customer;
import org.javamagazine.arquillianexample.cdi.util.JsfUtil;
import org.javamagazine.arquillianexample.cdi.util.PaginationHelper;
import org.javamagazine.arquillianexample.session.CustomerFacade;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

@Named("customerController")
@RequestScoped
public class CustomerController implements Serializable {

    private Customer current;
    private DataModel items = null;
    @EJB
    private org.javamagazine.arquillianexample.session.CustomerFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    
    private List<Customer> customerList;
    
    private String message;

    public CustomerController() {
      
    }
    
    @PostConstruct
    public void init(){
        setCustomerList(ejbFacade.findAll());
    }

    public Customer getSelected() {
        if (current == null) {
            current = new Customer();
            selectedItemIndex = -1;
        }
        return current;
    }

    /**
     * Finds <code>Customer</code> object by ID, and then sets the <code>current</code>
     * field to the found object.
     * @param id 
     */
    public void setCurrentById(BigDecimal id){
        Customer customer = ejbFacade.find(id);
        current = customer;
    }
    private CustomerFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView(Customer customer) {
        current = customer;
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Customer();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            System.out.println("CREATING CUSTOMER: " + current.getEmail());
            setMessage("Customer Successfully Created");
            JsfUtil.addSuccessMessage("Customer Successfully Created");
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Persistence Error");
            return null;
        }
    }

    public String prepareEdit(Customer customer) {
        current = customer;
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage("Customer Successfully Updated");
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Persistence Error");
            return null;
        }
    }

    public String destroy(Customer customer) {
        current = customer;
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }
    
    public void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage("Customer Successfully Deleted");
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Persistence Error");
        }
    }
    

    public void performDestroy(Customer customer) {
        current = customer;
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage("Customer Successfully Deleted");
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Persistence Error");
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public Customer getCustomer(BigDecimal id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = Customer.class)
    public static class CustomerControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            CustomerController controller = (CustomerController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "customerController");
            return controller.getCustomer(getKey(value));
        }

        BigDecimal getKey(String value) {
            BigDecimal key;
            key = BigDecimal.valueOf(Long.valueOf(value));
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Customer) {
                Customer o = (Customer) object;
                return o.getCustomerId().toString();
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Customer.class.getName());
            }
        }

    }

    /**
     * @return the customerList
     */
    public List<Customer> getCustomerList() {
        return customerList;
    }

    /**
     * @param customerList the customerList to set
     */
    public void setCustomerList(List<Customer> customerList) {
        this.customerList = customerList;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

}
