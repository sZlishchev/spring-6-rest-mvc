package guru.springframework.spring6restmvc.mappers;

import java.util.List;

import guru.springframework.spring6restmvc.enteties.Customer;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {
  
  Customer customerDtoToCustomer(final CustomerDTO customerDTO);
  
  List<CustomerDTO> customerListToCustomerDtoList(final List<Customer> customerList);
  
  CustomerDTO customerToCustomerDto(final Customer customer);
}
