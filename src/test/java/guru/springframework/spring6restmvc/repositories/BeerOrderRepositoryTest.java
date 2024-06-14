package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.enteties.Beer;
import guru.springframework.spring6restmvc.enteties.BeerOrder;
import guru.springframework.spring6restmvc.enteties.BeerOrderShipment;
import guru.springframework.spring6restmvc.enteties.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class BeerOrderRepositoryTest {
    
    @Autowired
    private BeerOrderRepository beerOrderRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private BeerRepository beerRepository;
    
    private Customer testCustomer;
    
    private Beer testBeer;

    @BeforeEach
    void setUp() {
        testCustomer = this.customerRepository.findAll().get(0);
        testBeer = this.beerRepository.findAll().get(0);
        
    }

   @Transactional
    @Test
    void testBeerOrderRepository() {
        final var beerOrder = BeerOrder.builder()
                .customerRef("Test Order")
                .customer(testCustomer)
                .beerOrderShipment(BeerOrderShipment.builder()
                        .trackingNumber("35235t")
                        .build())
                .build();

        final var savedBeerOrder = this.beerOrderRepository.save(beerOrder);
        
        assertNotNull(savedBeerOrder);
        assertNotNull(savedBeerOrder.getId());
        assertThat(savedBeerOrder.getCustomerRef()).isEqualTo("Test Order");
    }
}