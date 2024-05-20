package guru.springframework.spring6restmvc.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import guru.springframework.spring6restmvc.mappers.CustomerMapper;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import guru.springframework.spring6restmvc.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class CustomerServiceJPA implements CustomerService {
  
  private final CustomerRepository customerRepository;
  private final CustomerMapper customerMapper;

  @Override
  public List<CustomerDTO> getCustomersList() {
    return this.customerMapper.customerListToCustomerDtoList(this.customerRepository.findAll());
  }

  @Override
  public Optional<CustomerDTO> getCustomerById(UUID customerId) {
    return Optional.ofNullable(this.customerMapper.customerToCustomerDto(this.customerRepository.findById(customerId)
        .orElse(null)));
  }

  @Override
  public CustomerDTO saveNewCustomer(CustomerDTO customer) {
    return null;
  }

  @Override
  public void updateCustomer(UUID customerId, CustomerDTO customer) {

  }

  @Override
  public void deleteCustomerById(UUID customerId) {

  }

  @Override
  public void patchCustomerById(UUID customerId, CustomerDTO customer) {

  }
}
