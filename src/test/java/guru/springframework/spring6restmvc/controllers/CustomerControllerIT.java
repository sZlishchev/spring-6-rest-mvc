package guru.springframework.spring6restmvc.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import guru.springframework.spring6restmvc.enteties.Customer;
import guru.springframework.spring6restmvc.exception.NotFoundException;
import guru.springframework.spring6restmvc.mappers.CustomerMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class CustomerControllerIT {
  
  @Autowired
  private CustomerController customerController;
  
  @Autowired
  private CustomerRepository customerRepository;
  
  @Autowired
  private CustomerMapper customerMapper;

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

  @Rollback
  @Transactional
  @Test
  void testSaveNewCustomer() {
    final var customerToSave = CustomerDTO.builder()
            .name("Test name")
            .build();

    final var result = this.customerController.saveNewCustomer(customerToSave);

    assertThat(result).isNotNull();
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    final var resultId = result.getHeaders().getLocation().getPath().split("/");

    final var resultCustomer = this.customerRepository.findById(UUID.fromString(resultId[4])).get();

    assertThat(resultCustomer).isNotNull();
  }

  @Rollback
  @Transactional
  @Test
  void testUpdateCustomer() {
    final var customerToUpdate = this.customerMapper.customerToCustomerDto(this.customerRepository.findAll().get(0));

    final var newName = "UPDATED";

    final var customerId = customerToUpdate.getId();

    customerToUpdate.setId(null);
    customerToUpdate.setVersion(null);
    customerToUpdate.setName(newName);

    final var result = this.customerController.updateCustomer(customerId, customerToUpdate);

    assertThat(result).isNotNull();
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

    final var updatedCustomer = this.customerRepository.findById(customerId).get();

    assertThat(updatedCustomer).isNotNull();
    assertThat(updatedCustomer.getName()).isEqualTo(newName);
  }


  @Test
  void testUpdateCustomerNotFound() {
    assertThrows(NotFoundException.class, () -> this.customerController.updateCustomer(UUID.randomUUID(), CustomerDTO.builder().build()));
  }

  @Rollback
  @Transactional
  @Test
  void testDeleteCustomerById() {
    final var customer = this.customerRepository.findAll().get(0);

    final var response = this.customerController.deleteCustomer(customer.getId());

    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(this.customerRepository.findById(customer.getId())).isEmpty();
  }

  @Test
  public void testDeleteCustomerByIdNotFound() {
    assertThrows(NotFoundException.class, () -> this.customerController.deleteCustomer(UUID.randomUUID()));
  }

  @Rollback
  @Transactional
  @Test
  void testPatchCustomer() {
    final var customerToUpdate = this.customerRepository.findAll().get(0);

    final var customerForUpdate = CustomerDTO.builder().name("Updated Name").build();

    final var response = this.customerController.patchCustomer(customerToUpdate.getId(), customerForUpdate);

    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final var updatedBeer = this.customerRepository.findById(customerToUpdate.getId()).get();

    assertThat(updatedBeer.getName()).isEqualTo(customerForUpdate.getName());

    assertThat(updatedBeer.getId()).isEqualTo(customerToUpdate.getId());
    assertThat(updatedBeer.getVersion()).isEqualTo(customerToUpdate.getVersion());
  }

  @Test
  void testPatchCustomerNotFound() {
    assertThrows(NotFoundException.class, () -> this.customerController.patchCustomer(UUID.randomUUID(), CustomerDTO.builder().build()));
  }
}