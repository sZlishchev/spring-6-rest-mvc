package guru.springframework.spring6restmvc.bootstrap;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import guru.springframework.spring6restmvc.enteties.Beer;
import guru.springframework.spring6restmvc.enteties.Customer;
import guru.springframework.spring6restmvc.model.BeerCSVRecord;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import guru.springframework.spring6restmvc.services.BeerCSVService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

  private final BeerRepository beerRepository;
  
  private final CustomerRepository customerRepository;
  
  private final BeerCSVService beerCSVService;
  
  
  @Transactional
  @Override
  public void run(String... args) throws Exception {
    this.loadBeerData();
    this.loadCsvData();
    this.loadCustomerData();
  }

  private void loadCsvData() throws FileNotFoundException {
    if (beerRepository.count() < 10){
      File file = ResourceUtils.getFile("classpath:csvdata/beer.csv");

      List<BeerCSVRecord> recs = this.beerCSVService.convertCSV(file);

      recs.forEach(beerCSVRecord -> {
        BeerStyle beerStyle = switch (beerCSVRecord.getStyle()) {
          case "American Pale Lager" -> BeerStyle.LAGER;
          case "American Pale Ale (APA)", "American Black Ale", "Belgian Dark Ale", "American Blonde Ale" ->
                  BeerStyle.ALE;
          case "American IPA", "American Double / Imperial IPA", "Belgian IPA" -> BeerStyle.IPA;
          case "American Porter" -> BeerStyle.PORTER;
          case "Oatmeal Stout", "American Stout" -> BeerStyle.STOUT;
          case "Saison / Farmhouse Ale" -> BeerStyle.SAISON;
          case "Fruit / Vegetable Beer", "Winter Warmer", "Berliner Weissbier" -> BeerStyle.WHEAT;
          case "English Pale Ale" -> BeerStyle.PALE_ALE;
          default -> BeerStyle.PILSNER;
        };

        beerRepository.save(Beer.builder()
                .beerName(StringUtils.abbreviate(beerCSVRecord.getBeer(), 50))
                .beerStyle(beerStyle)
                .price(BigDecimal.TEN)
                .upc(beerCSVRecord.getRow().toString())
                .quantityOnHand(beerCSVRecord.getCount())
                .build());
      });
    }
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
