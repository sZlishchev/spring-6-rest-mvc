package guru.springframework.spring6restmvc.controllers;


import static guru.springframework.spring6restmvc.controllers.HttpBasicConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.exception.NotFoundException;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class BeerControllerIT {

    @Autowired
    private BeerController beerController;

    @Autowired
    private BeerRepository beerRepository;

    @Autowired
    private BeerMapper beerMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }

    @Test
    void testGetBeerById() {
        final var beerId = this.beerRepository.findAll().get(0).getId();

        final var beerById = this.beerController.getBeerById(beerId);

        assertThat(beerById).isNotNull();
    }

    @Test
    void testGetBeerByIdNotFound() {
        assertThrows(NotFoundException.class, () -> this.beerController.getBeerById(UUID.randomUUID()));
    }

    @Test
    void testBeerList() {
        final var beerList = this.beerController.getBeerList(null, null, null, null, 1000);

        assertThat(beerList).isNotNull();
        assertThat(beerList.getContent().size()).isEqualTo(1000);
    }

    @Rollback
    @Transactional
    @Test
    void testBeerEmptyList() {
        this.beerRepository.deleteAll();

        final var beerList = this.beerController.getBeerList(null, null, null, null, null);

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

    @Test
    void testPatchBeerTooLongName() throws Exception {
        final var beer = this.beerRepository.findAll().get(0);

        final var beerMap = new HashMap<>();
        beerMap.put("beerName", "name0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");

        this.mockMvc.perform(patch(BeerController.BEER_PATH_ID, beer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(beerMap))
                        .with(httpBasic(USER, PASSWORD)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)))
                .andReturn();
    }

    @Test
    void testGetBeersByName() throws Exception {
        this.mockMvc.perform(get(BeerController.BEER_PATH)
                        .queryParam("beerName", "IPA")
                .queryParam("pageSize", "500")
                        .with(httpBasic(USER, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(336)));
    }

    @Test
    void testGetBeersByStyle() throws Exception {
        this.mockMvc.perform(get(BeerController.BEER_PATH)
                        .queryParam("beerStyle", BeerStyle.STOUT.name())
                        .queryParam("pageSize", "500")
                        .with(httpBasic(USER, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(57)));
    }

    @Test
    void testGetBeersByNameAndStyle() throws Exception {
        this.mockMvc.perform(get(BeerController.BEER_PATH)
                        .queryParam("beerStyle", BeerStyle.IPA.name())
                        .queryParam("beerName", "IPA")
                        .queryParam("pageSize", "500")
                        .with(httpBasic(USER, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(310)));
    }

    @Test
    void testGetBeersShowInventoryFalse() throws Exception {
        this.mockMvc.perform(get(BeerController.BEER_PATH)
                        .queryParam("beerStyle", BeerStyle.IPA.name())
                        .queryParam("beerName", "IPA")
                        .queryParam("showInventory", "false")
                        .queryParam("pageSize", "500")
                        .with(httpBasic(USER, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(310)))
                .andExpect(jsonPath("$.content.[0].quantityOnHand").value(IsNull.nullValue()));
    }

    @Test
    void testGetBeersShowInventoryTrue() throws Exception {
        this.mockMvc.perform(get(BeerController.BEER_PATH)
                        .queryParam("beerStyle", BeerStyle.IPA.name())
                        .queryParam("beerName", "IPA")
                        .queryParam("showInventory", "true")
                .queryParam("pageSize", "500")
                        .with(httpBasic(USER, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(310)))
                .andExpect(jsonPath("$.content.[0].quantityOnHand").value(IsNull.notNullValue()));
    }

    @Test
    void testGetBeersShowInventoryTruePage2() throws Exception {
        this.mockMvc.perform(get(BeerController.BEER_PATH)
                        .queryParam("beerStyle", BeerStyle.IPA.name())
                        .queryParam("beerName", "IPA")
                        .queryParam("showInventory", "true")
                        .queryParam("pageNumber", "2")
                        .queryParam("pageSize", "25")
                        .with(httpBasic(USER, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(25)))
                .andExpect(jsonPath("$.content.[0].quantityOnHand").value(IsNull.notNullValue()));
    }

    @Test
    void testGetBeers_invalidCredentials() throws Exception {
        this.mockMvc.perform(get(BeerController.BEER_PATH)
                        .with(httpBasic(WRONG_USER, WRONG_PASSWORD)))
                .andExpect(status().isUnauthorized());
    }
}