package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.bootstrap.DataInitializer;
import guru.springframework.spring6restmvc.enteties.Beer;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.services.BeerCSVService;
import guru.springframework.spring6restmvc.services.impl.BeerCSVServiceImpl;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({DataInitializer.class, BeerCSVServiceImpl.class})
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

    @Test
    void testGetAllBeersByName() {
        final var list = this.beerRepository.findAllByBeerNameIsLikeIgnoreCase("%IPA%");

        assertNotNull(list);
        assertThat(list).hasSize(336);
    }

    @Test
    void testGetAllBeersByStyle() {
        final var list = this.beerRepository.findAllByBeerStyle(BeerStyle.STOUT);
        
        assertNotNull(list);
        assertThat(list).hasSize(57);
    }

    @Test
    void testGetAllBeersByNameAndStyle() {
        final var list = this.beerRepository.findAllByBeerNameIsLikeIgnoreCaseAndBeerStyle("%IPA%", BeerStyle.IPA);

        assertNotNull(list);
        assertThat(list).hasSize(310);
    }
}