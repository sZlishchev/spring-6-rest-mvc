package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.enteties.Beer;
import guru.springframework.spring6restmvc.model.BeerStyle;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class BeerRepositoryTest {

    @Autowired
    private BeerRepository beerRepository;

    @Test
    void testSaveBeer() {
        final var savedBeer = this.beerRepository.save(Beer.builder()
                .beerName("New name")
                .beerStyle(BeerStyle.LAGER)
                .upc("235235")
                .price(BigDecimal.valueOf(55))
                .build());
        this.beerRepository.flush();

        assertThat(savedBeer).isNotNull();
        assertThat(savedBeer.getId()).isNotNull();
        assertThat(savedBeer.getBeerName()).isEqualTo("New name");
    }

    @Test
    void testSaveBeerTooLongName() {

        assertThrows(ConstraintViolationException.class, () -> {
            this.beerRepository.save(Beer.builder()
                    .beerName("New name 1111112321312412412414124124124111111232131241241241412412412411111123213124124124141241241241111112321312412412414124124124")
                    .beerStyle(BeerStyle.LAGER)
                    .upc("235235")
                    .price(BigDecimal.valueOf(55))
                    .build());
            
            this.beerRepository.flush();
        });
    }
}