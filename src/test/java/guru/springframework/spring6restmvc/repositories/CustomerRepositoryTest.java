package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.enteties.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerRepositoryTest {
    
    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void testSaveCustomer() {
        final var savedCustomer = this.customerRepository.save(Customer.builder()
                .name("New name")
                .build());
        
        assertThat(savedCustomer).isNotNull();
        assertThat(savedCustomer.getId()).isNotNull();
        assertThat(savedCustomer.getName()).isEqualTo("New name");
    }
}