package guru.springframework.spring6restmvc.controllers;

import guru.springframework.spring6restmvc.model.Customer;
import guru.springframework.spring6restmvc.services.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/v1/customers")
public class CustomerController {
    private final CustomerService customerService;
    
    @PatchMapping("{customerId}")
    public ResponseEntity<Customer> patchCustomer(@PathVariable UUID customerId, @RequestBody Customer customer) {
        this.customerService.patchCustomerById(customerId, customer);
        
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @DeleteMapping("{customerId}")
    public ResponseEntity<Customer> deleteCustomer(@PathVariable UUID customerId) {
        this.customerService.deleteCustomerById(customerId);
        
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @PutMapping("{customerId}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable UUID customerId, @RequestBody Customer customer) {
        
        this.customerService.updateCustomer(customerId, customer);
        
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public List<Customer> getCustomersList(){
        return this.customerService.getCustomersList();
    }

    @PostMapping
    public ResponseEntity<Customer> saveNewCustomer(@RequestBody Customer customer) {
        final var savedCustomer = this.customerService.saveNewCustomer(customer);
        
        final var headers = new HttpHeaders();
        headers.add("Location", "/api/v1/customers/" + savedCustomer.getId());
        
        return new ResponseEntity<>(savedCustomer, headers, HttpStatus.CREATED);
    }
    
    @RequestMapping(path = "{customerId}",method = RequestMethod.GET)
    public Customer getCustomersList(@PathVariable UUID customerId){
        return this.customerService.getCustomerById(customerId);
    }
}
