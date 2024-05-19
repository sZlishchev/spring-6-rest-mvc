package guru.springframework.spring6restmvc.controllers;

import guru.springframework.spring6restmvc.exception.NotFoundException;
import guru.springframework.spring6restmvc.model.Customer;
import guru.springframework.spring6restmvc.services.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping()
public class CustomerController {
    public static final String CUSTOMER_PATH = "/api/v1/customers";

    public static final String CUSTOMER_PATH_ID = CUSTOMER_PATH + "/{customerId}";
    
    private final CustomerService customerService;
    
    @PatchMapping(CUSTOMER_PATH_ID)
    public ResponseEntity<Customer> patchCustomer(@PathVariable UUID customerId, @RequestBody Customer customer) {
        this.customerService.patchCustomerById(customerId, customer);
        
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @DeleteMapping(CUSTOMER_PATH_ID)
    public ResponseEntity<Customer> deleteCustomer(@PathVariable UUID customerId) {
        this.customerService.deleteCustomerById(customerId);
        
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @PutMapping(CUSTOMER_PATH_ID)
    public ResponseEntity<Customer> updateCustomer(@PathVariable UUID customerId, @RequestBody Customer customer) {
        
        this.customerService.updateCustomer(customerId, customer);
        
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @GetMapping(CUSTOMER_PATH)
    public List<Customer> getCustomersList(){
        return this.customerService.getCustomersList();
    }

    @PostMapping(CUSTOMER_PATH)
    public ResponseEntity<Customer> saveNewCustomer(@RequestBody Customer customer) {
        final var savedCustomer = this.customerService.saveNewCustomer(customer);
        
        final var headers = new HttpHeaders();
        headers.add("Location", "/api/v1/customers/" + savedCustomer.getId());
        
        return new ResponseEntity<>(savedCustomer, headers, HttpStatus.CREATED);
    }
    
    @GetMapping(CUSTOMER_PATH_ID)
    public Customer getCustomerById(@PathVariable UUID customerId){
        return this.customerService.getCustomerById(customerId).orElseThrow(NotFoundException::new);
    }
}
