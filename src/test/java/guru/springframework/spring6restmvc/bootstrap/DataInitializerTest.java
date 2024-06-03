package guru.springframework.spring6restmvc.bootstrap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.opencsv.bean.CsvToBeanBuilder;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;

import guru.springframework.spring6restmvc.services.BeerCSVService;
import guru.springframework.spring6restmvc.services.impl.BeerCSVServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(BeerCSVServiceImpl.class)
class DataInitializerTest {
  
  @Autowired
  private BeerRepository beerRepository;
  
  @Autowired 
  private CustomerRepository customerRepository;
  
  @Autowired
  private BeerCSVService beerCSVService;
  
  private DataInitializer dataInitializer;

  @BeforeEach
  void beforeEach() {
    this.dataInitializer = new DataInitializer(beerRepository, customerRepository, beerCSVService);
  }
  
  @Test
  void testInitialization() throws Exception {
    this.dataInitializer.run();
    
    assertThat(this.beerRepository.count()).isEqualTo(2413);
    assertThat(this.customerRepository.count()).isEqualTo(3);
  }
}