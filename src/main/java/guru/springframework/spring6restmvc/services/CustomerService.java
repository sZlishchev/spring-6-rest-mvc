package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.Customer;

import java.util.List;
import java.util.UUID;

public interface CustomerService {
    
    List<Customer> getCustomersList();
    
    Customer getCustomerById(final UUID customerId);

    Customer saveNewCustomer(Customer customer);

    void updateCustomer(UUID customerId, Customer customer);

    void deleteCustomerById(UUID customerId);

    void patchCustomerById(UUID customerId, Customer customer);
}
