package guru.springframework.spring6restmvc.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import guru.springframework.spring6restmvc.exception.NotFoundException;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class CustomerControllerIT {
  
  @Autowired
  private CustomerController customerController;
  
  @Autowired
  private CustomerRepository customerRepository;

  @Test
  void testGetCustomerById() {
    final var customerId = this.customerRepository.findAll().get(0).getId();

    final var customerById = this.customerController.getCustomerById(customerId);
    
    assertThat(customerById).isNotNull();
  }

  @Test
  void testGetCustomerByIdNotFound() {
    assertThrows(NotFoundException.class, () -> this.customerController.getCustomerById(UUID.randomUUID()));
  }

  @Test
  void testGetCustomerList() {
    final var customers = this.customerController.getCustomersList();
    
    assertThat(customers).isNotNull();
    assertThat(customers.size()).isEqualTo(3);
  }
  
  @Rollback
  @Transactional
  @Test
  void testGetCustomerEmptyList() {
    this.customerRepository.deleteAll();
    
    final var customers = this.customerController.getCustomersList();
    
    assertThat(customers).isNotNull();
    assertThat(customers).isEmpty();
  }

}