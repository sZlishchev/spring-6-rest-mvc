package guru.springframework.spring6restmvc.controllers;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import guru.springframework.spring6restmvc.enteties.Beer;
import guru.springframework.spring6restmvc.exception.NotFoundException;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class BeerControllerIT {
  
  @Autowired
  private BeerController beerController;
  
  @Autowired
  private BeerRepository beerRepository;
  
  @Autowired
  private BeerMapper beerMapper;

  @Test
  void testGetBeerById() {
    final var beerId = this.beerRepository.findAll().get(0).getId();
    
    final var beerById = this.beerController.getBeerById(beerId);
    
    assertThat(beerById).isNotNull();
  }

  @Test
  void testGerBeerByIdNotFound() {
    assertThrows(NotFoundException.class, ()-> this.beerController.getBeerById(UUID.randomUUID()));
  }

  @Test
  void testBeerList() {
    final var beerList = this.beerController.getBeerList();

    assertThat(beerList).isNotNull();
    assertThat(beerList.size()).isEqualTo(3);
  }
  
  @Rollback
  @Transactional
  @Test
  void testBeerEmptyList() {
    this.beerRepository.deleteAll();
    
    final var beerList = this.beerController.getBeerList();
    
    assertThat(beerList).isNotNull();
    assertThat(beerList).isEmpty();
  }

  @Rollback
  @Transactional
  @Test
  void testSaveNewBeer() {
    final var beerToSave = BeerDTO.builder()
        .beerName("Test name")
        .build();
    
    final var result = this.beerController.saveNewBeer(beerToSave);
    
    assertThat(result).isNotNull();
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    
    final var resultId = result.getHeaders().getLocation().getPath().split("/");
    
    final var resultBeer = this.beerRepository.findById(UUID.fromString(resultId[4])).get();
    
    assertThat(resultBeer).isNotNull();
  }

  @Test
  void testUpdateBeer() {
    final var beerToUpdate = this.beerMapper.beerToBeerDTO(this.beerRepository.findAll().get(0));
    
    final var newName = "UPDATED";
    
    final var beerId = beerToUpdate.getId();
    
    beerToUpdate.setId(null);
    beerToUpdate.setVersion(null);
    beerToUpdate.setBeerName(newName);
    
    final var result = this.beerController.updateBeer(beerId, beerToUpdate);
    
    assertThat(result).isNotNull();
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    
    final var updatedBeer = this.beerRepository.findById(beerId).get();
    
    assertThat(updatedBeer).isNotNull();
    assertThat(updatedBeer.getBeerName()).isEqualTo(newName);
  }

  @Test
  void testUpdateBeerNotFound() {
    assertThrows(NotFoundException.class, () -> this.beerController.updateBeer(UUID.randomUUID(), BeerDTO.builder().build()));
  }
}