package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.enteties.Beer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BeerRepositoryTest {
    
    @Autowired
    private BeerRepository beerRepository;

    @Test
    void testSaveBeer() {
        final var savedBeer = this.beerRepository.save(Beer.builder()
                .beerName("New name")
                .build());
        
        assertThat(savedBeer).isNotNull();
        assertThat(savedBeer.getId()).isNotNull();
        assertThat(savedBeer.getBeerName()).isEqualTo("New name");
    }
}