package guru.springframework.spring6restmvc.bootstrap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import guru.springframework.spring6restmvc.repositories.BeerRepository;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class DataInitializerTest {
  
  @Autowired
  private BeerRepository beerRepository;
  
  @Autowired 
  private CustomerRepository customerRepository;
  
  private DataInitializer dataInitializer;

  @BeforeEach
  void beforeEach() {
    this.dataInitializer = new DataInitializer(beerRepository, customerRepository);
  }
  
  @Test
  void testInitialization() throws Exception {
    this.dataInitializer.run();
    
    assertThat(this.beerRepository.count()).isEqualTo(3);
    assertThat(this.customerRepository.count()).isEqualTo(3);
  }
}