package guru.springframework.spring6restmvc.bootstrap;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import guru.springframework.spring6restmvc.enteties.Beer;
import guru.springframework.spring6restmvc.enteties.Customer;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

  private final BeerRepository beerRepository;
  
  private final CustomerRepository customerRepository;
  
  
  @Override
  public void run(String... args) throws Exception {
    this.loadBeerData();
    this.loadCustomerData();
  }
  
  private void loadBeerData() {
    if (this.beerRepository.count() == 0) {
      final var lager = Beer.builder()
          .beerName("Lager Classic")
          .beerStyle(BeerStyle.LAGER)
          .upc("12324")
          .price(new BigDecimal("56.79"))
          .quantityOnHand(10)
          .createdDate(LocalDateTime.now())
          .updatedDate(LocalDateTime.now())
          .build();

      final var ale = Beer.builder()
          .beerName("Ale Classic")
          .beerStyle(BeerStyle.ALE)
          .upc("52462")
          .price(new BigDecimal("34.79"))
          .quantityOnHand(6)
          .createdDate(LocalDateTime.now())
          .updatedDate(LocalDateTime.now())
          .build();

      final var pilsner = Beer.builder()
          .beerName("Pilsner Classic")
          .beerStyle(BeerStyle.PILSNER)
          .upc("1532")
          .price(new BigDecimal("77.99"))
          .quantityOnHand(12)
          .createdDate(LocalDateTime.now())
          .updatedDate(LocalDateTime.now())
          .build();

      this.beerRepository.saveAll(List.of(lager, ale, pilsner));
    }
  }

  private void loadCustomerData() {
    if(this.customerRepository.count() == 0) {
      final var oleg = Customer.builder()
          .name("Pan Oleg")
          .createdDateTime(LocalDateTime.now())
          .lastUpdateDateTime(LocalDateTime.now())
          .build();

      final var pavlo = Customer.builder()
          .name("Pan Pavlo")
          .createdDateTime(LocalDateTime.now())
          .lastUpdateDateTime(LocalDateTime.now())
          .build();

      final var zlishchev = Customer.builder()
          .name("Old Zlishchev")
          .createdDateTime(LocalDateTime.now())
          .lastUpdateDateTime(LocalDateTime.now())
          .build();

      this.customerRepository.saveAll(List.of(oleg, pavlo, zlishchev));
    }
  }
}
