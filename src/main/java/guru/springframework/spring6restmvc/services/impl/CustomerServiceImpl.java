package guru.springframework.spring6restmvc.services.impl;

import guru.springframework.spring6restmvc.model.Customer;
import guru.springframework.spring6restmvc.services.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerServiceImpl implements CustomerService {
    
    private Map<UUID, Customer> customerMap;

    public CustomerServiceImpl() {
        this.customerMap = new HashMap<>();
        
        final var oleg = Customer.builder()
                .id(UUID.randomUUID())
                .name("Pan Oleg")
                .version(1)
                .createdDateTime(LocalDateTime.now())
                .lastUpdateDateTime(LocalDateTime.now())
                .build();

        final var pavlo = Customer.builder()
                .id(UUID.randomUUID())
                .name("Pan Pavlo")
                .version(1)
                .createdDateTime(LocalDateTime.now())
                .lastUpdateDateTime(LocalDateTime.now())
                .build();

        final var zlishchev = Customer.builder()
                .id(UUID.randomUUID())
                .name("Old Zlishchev")
                .version(1)
                .createdDateTime(LocalDateTime.now())
                .lastUpdateDateTime(LocalDateTime.now())
                .build();
        
        customerMap.put(oleg.getId(), oleg);
        customerMap.put(pavlo.getId(), pavlo);
        customerMap.put(zlishchev.getId(), zlishchev);
    }

    @Override
    public List<Customer> getCustomersList() {
        return new ArrayList<>(this.customerMap.values());
    }

    @Override
    public Optional<Customer> getCustomerById(UUID customerId) {
        return Optional.of(this.customerMap.get(customerId));
    }

    @Override
    public Customer saveNewCustomer(Customer customer) {
        final var savedCustomer = Customer.builder()
                .id(UUID.randomUUID())
                .createdDateTime(LocalDateTime.now())
                .lastUpdateDateTime(LocalDateTime.now())
                .name(customer.getName())
                .version(customer.getVersion())
                .build();
        
        this.customerMap.put(savedCustomer.getId(), savedCustomer);
        
        return savedCustomer;
    }

    @Override
    public void updateCustomer(UUID customerId, Customer customer) {
        final var customerToUpdate = this.customerMap.get(customerId);
        
        customerToUpdate.setName(customer.getName());
        customerToUpdate.setLastUpdateDateTime(LocalDateTime.now());
    }

    @Override
    public void deleteCustomerById(UUID customerId) {
        this.customerMap.remove(customerId);
    }

    @Override
    public void patchCustomerById(UUID customerId, Customer customer) {
        final var customerToUpdate = this.customerMap.get(customerId);
        
        if(StringUtils.hasText(customer.getName())) {
            customerToUpdate.setName(customer.getName());
        }
        
        customerToUpdate.setLastUpdateDateTime(LocalDateTime.now());
    }
}
