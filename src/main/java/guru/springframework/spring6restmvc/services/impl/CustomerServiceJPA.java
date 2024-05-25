package guru.springframework.spring6restmvc.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import guru.springframework.spring6restmvc.mappers.CustomerMapper;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import guru.springframework.spring6restmvc.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    return this.customerMapper.customerToCustomerDto(this.customerRepository.save(this.customerMapper.customerDtoToCustomer(customer)));
  }

  @Override
  public Optional<CustomerDTO> updateCustomer(UUID customerId, CustomerDTO customer) {
    final var atomicReference = new AtomicReference<Optional<CustomerDTO>>();

    this.customerRepository.findById(customerId).ifPresentOrElse( customerToUdpate -> {
      customerToUdpate.setName(customer.getName());
      customerToUdpate.setLastUpdateDateTime(LocalDateTime.now());

      atomicReference.set(Optional.of(this.customerMapper.customerToCustomerDto(this.customerRepository.save(customerToUdpate))));
    }, () -> {
      atomicReference.set(Optional.empty());
    });

    return atomicReference.get();
  }

  @Override
  public Boolean deleteCustomerById(UUID customerId) {
    if (this.customerRepository.existsById(customerId)) {
      this.customerRepository.deleteById(customerId);
      
      return true;
    }
    
    return false;
  }

  @Override
  public Optional<CustomerDTO> patchCustomerById(UUID customerId, CustomerDTO customer) {
      final var atomicReference = new AtomicReference<Optional<CustomerDTO>>();
      
      this.customerRepository.findById(customerId).ifPresentOrElse(customerToUpdate -> {
        if (StringUtils.hasText(customer.getName())) {
          customerToUpdate.setName(customer.getName());
          customerToUpdate.setLastUpdateDateTime(LocalDateTime.now());
        }
        atomicReference.set(Optional.of(this.customerMapper.customerToCustomerDto(this.customerRepository.save(customerToUpdate))));
      }, () -> atomicReference.set(Optional.empty()));
      
      return atomicReference.get();
  }
}
