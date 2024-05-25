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
import org.springframework.http.ResponseEntity;
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
  void testGetBeerByIdNotFound() {
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

  @Rollback
  @Transactional
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
  
  @Rollback
  @Transactional
  @Test
  void testDeleteBeerById() {
    final var beer = this.beerRepository.findAll().get(0);

    final var response = this.beerController.deleteBeer(beer.getId());
    
    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(this.beerRepository.findById(beer.getId())).isEmpty();
  }
  
  @Test
  public void testDeleteBeerByIdNotFound() {
    assertThrows(NotFoundException.class, () -> this.beerController.deleteBeer(UUID.randomUUID()));
  }

  @Rollback
  @Transactional
  @Test
  void testPatchBeer() {
    final var beerToUpdate = this.beerRepository.findAll().get(0);
    
    final var beerForUpdate = BeerDTO.builder().beerName("Updated Name").quantityOnHand(99).build();

    ResponseEntity<BeerDTO> response = this.beerController.patchBeer(beerToUpdate.getId(), beerForUpdate);
    
    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    
    final var updatedBeer = this.beerRepository.findById(beerToUpdate.getId()).get();
    
    assertThat(updatedBeer.getBeerName()).isEqualTo(beerForUpdate.getBeerName());
    assertThat(updatedBeer.getQuantityOnHand()).isEqualTo(beerForUpdate.getQuantityOnHand());
    
    assertThat(updatedBeer.getId()).isEqualTo(beerToUpdate.getId());
    assertThat(updatedBeer.getBeerStyle()).isEqualTo(beerToUpdate.getBeerStyle());
    assertThat(updatedBeer.getUpc()).isEqualTo(beerToUpdate.getUpc());
    assertThat(updatedBeer.getPrice()).isEqualTo(beerToUpdate.getPrice());
    assertThat(updatedBeer.getVersion()).isEqualTo(beerToUpdate.getVersion());
  }

  @Test
  void testPatchBeerNotFound() {
    assertThrows(NotFoundException.class, () -> this.beerController.patchBeer(UUID.randomUUID(), BeerDTO.builder().build()));
  }
}