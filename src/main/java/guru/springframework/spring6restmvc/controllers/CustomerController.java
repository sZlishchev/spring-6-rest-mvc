package guru.springframework.spring6restmvc.controllers;

import guru.springframework.spring6restmvc.exception.NotFoundException;
import guru.springframework.spring6restmvc.model.CustomerDTO;
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
    public ResponseEntity<CustomerDTO> patchCustomer(@PathVariable UUID customerId, @RequestBody CustomerDTO customer) {
        this.customerService.patchCustomerById(customerId, customer).orElseThrow(NotFoundException::new);
        
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @DeleteMapping(CUSTOMER_PATH_ID)
    public ResponseEntity<CustomerDTO> deleteCustomer(@PathVariable UUID customerId) {
        if (!this.customerService.deleteCustomerById(customerId)) {
            throw new NotFoundException();
        }
        
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @PutMapping(CUSTOMER_PATH_ID)
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable UUID customerId, @RequestBody CustomerDTO customer) {
        
        this.customerService.updateCustomer(customerId, customer).orElseThrow(NotFoundException::new);
        
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @GetMapping(CUSTOMER_PATH)
    public List<CustomerDTO> getCustomersList(){
        return this.customerService.getCustomersList();
    }

    @PostMapping(CUSTOMER_PATH)
    public ResponseEntity<CustomerDTO> saveNewCustomer(@RequestBody CustomerDTO customer) {
        final var savedCustomer = this.customerService.saveNewCustomer(customer);
        
        final var headers = new HttpHeaders();
        headers.add("Location", "/api/v1/customers/" + savedCustomer.getId());
        
        return new ResponseEntity<>(savedCustomer, headers, HttpStatus.CREATED);
    }
    
    @GetMapping(CUSTOMER_PATH_ID)
    public CustomerDTO getCustomerById(@PathVariable UUID customerId){
        return this.customerService.getCustomerById(customerId).orElseThrow(NotFoundException::new);
    }
}
