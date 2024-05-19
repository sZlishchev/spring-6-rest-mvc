package guru.springframework.spring6restmvc.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.model.Customer;
import guru.springframework.spring6restmvc.services.CustomerService;
import guru.springframework.spring6restmvc.services.impl.CustomerServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private CustomerService customerService;
    
    @Captor
    private ArgumentCaptor<UUID> uuidCaptor;
    @Captor
    private ArgumentCaptor<Customer> customerCaptor;
    
    private final CustomerServiceImpl customerServiceImpl = new CustomerServiceImpl();

    @Test
    void testGetCustomerByIdNotFound() throws Exception {
        when(this.customerService.getCustomerById(any(UUID.class))).thenReturn(Optional.empty());
        
        this.mockMvc.perform(get(CustomerController.CUSTOMER_PATH_ID, UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void testPatchCustomer() throws Exception {
        final var customerId = UUID.randomUUID();
        
        final var customerPropertiesMap = new HashMap<>();
        customerPropertiesMap.put("name", "John");
        
        this.mockMvc.perform(patch(CustomerController.CUSTOMER_PATH_ID, customerId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(customerPropertiesMap)))
                .andExpect(status().isOk());
        
        verify(this.customerService).patchCustomerById(uuidCaptor.capture(), this.customerCaptor.capture());
        
        assertThat(customerPropertiesMap.get("name")).isEqualTo(customerCaptor.getValue().getName());
        assertThat(customerId).isEqualTo(uuidCaptor.getValue());
    }
    
    @Test
    void testDeleteCustomer() throws Exception {
        final var customerId = UUID.randomUUID();
        
        this.mockMvc.perform(delete(CustomerController.CUSTOMER_PATH_ID, customerId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        verify(this.customerService).deleteCustomerById(customerId);
    }
    
    @Test
    void testCustomerUpdate() throws Exception {
        final var customer = this.customerServiceImpl.getCustomersList().get(0);

        this.mockMvc.perform(put(CustomerController.CUSTOMER_PATH_ID, customer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(customer)))
                .andExpect(status().isOk());

        verify(this.customerService).updateCustomer(customer.getId(), customer);
    }
    
    @Test
    void createCustomer() throws Exception {
        final var customer = this.customerServiceImpl.getCustomersList().get(0);
        customer.setId(null);
        customer.setVersion(null);
        
        when(this.customerService.saveNewCustomer(customer)).thenReturn(this.customerServiceImpl.getCustomersList().get(1));
        
        this.mockMvc.perform(post(CustomerController.CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().exists("Location"));
    }
    
    @Test
    void getCustomersList() throws Exception {
        when(this.customerService.getCustomersList()).thenReturn(this.customerServiceImpl.getCustomersList());
        
        mockMvc.perform(get(CustomerController.CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(3)));
    }

    @Test
    void getCustomerById() throws Exception {
        final var testCustomer = this.customerServiceImpl.getCustomersList().get(0);
        
        when(this.customerService.getCustomerById(testCustomer.getId())).thenReturn(Optional.of(testCustomer));
        
        this.mockMvc.perform(get(CustomerController.CUSTOMER_PATH_ID, testCustomer.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testCustomer.getId().toString())))
                .andExpect(jsonPath("$.name", is(testCustomer.getName())));
    }
}