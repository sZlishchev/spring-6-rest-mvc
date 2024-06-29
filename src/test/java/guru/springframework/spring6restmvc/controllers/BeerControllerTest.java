package guru.springframework.spring6restmvc.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.config.SpringSecConfig;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.services.BeerService;
import guru.springframework.spring6restmvc.services.impl.BeerServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static guru.springframework.spring6restmvc.controllers.HttpBasicConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeerController.class)
@Import(SpringSecConfig.class)
class BeerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BeerService beerService;

    @Captor
    private ArgumentCaptor<UUID> uuidCaptor;

    @Captor
    ArgumentCaptor<BeerDTO> beerCaptor;

    private final BeerServiceImpl beerServiceImpl = new BeerServiceImpl();

    @Test
    void testGetBeerByIdNotFound() throws Exception {

        when(this.beerService.getBeerById(any(UUID.class))).thenReturn(Optional.empty());

        this.mockMvc.perform(get(BeerController.BEER_PATH_ID, UUID.randomUUID())
                        .with(JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isNotFound());
    }

    @Test
    void testPatchBeer() throws Exception {
        final var beerId = UUID.randomUUID();
        final var beer = BeerDTO.builder().beerName("Obolon").build();

        when(this.beerService.patchBeerById(beerId, beer)).thenReturn(Optional.of(beer));

        this.mockMvc.perform(patch(BeerController.BEER_PATH_ID, beerId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(beer))
                        .with(JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isOk());

        verify(this.beerService).patchBeerById(uuidCaptor.capture(), beerCaptor.capture());

        assertThat(beer.getBeerName()).isEqualTo(beerCaptor.getValue().getBeerName());
        assertThat(beerId).isEqualTo(uuidCaptor.getValue());
    }

    @Test
    void testDeleteBeer() throws Exception {
        final var beerId = UUID.randomUUID();

        when(this.beerService.deleteBeerById(beerId)).thenReturn(true);

        this.mockMvc.perform(delete(BeerController.BEER_PATH_ID, beerId)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isOk());

        verify(this.beerService).deleteBeerById(beerId);
    }

    @Test
    void testBeerUpdate() throws Exception {
        final var beer = this.beerServiceImpl.getBeerPage(null, null, true, 1, 25).getContent().get(0);

        when(this.beerService.updateBeer(beer.getId(), beer))
                .thenReturn(Optional.ofNullable(BeerDTO.builder().build()));

        this.mockMvc.perform(put(BeerController.BEER_PATH_ID, beer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(beer))
                        .with(JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isOk());

        verify(this.beerService).updateBeer(beer.getId(), beer);
    }

    @Test
    void testBeerUpdateNullName() throws Exception {
        final var beer = this.beerServiceImpl.getBeerPage(null, null, true, 1, 25).getContent().get(0);
        beer.setBeerName(null);

        this.mockMvc.perform(put(BeerController.BEER_PATH_ID, beer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(beer))
                        .with(JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(2)));

        verify(this.beerService, never()).updateBeer(beer.getId(), beer);
    }

    @Test
    void testBeerUpdateBlankName() throws Exception {
        final var beer = this.beerServiceImpl.getBeerPage(null, null, true, 1, 25).getContent().get(0);
        beer.setBeerName("   ");

        this.mockMvc.perform(put(BeerController.BEER_PATH_ID, beer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(beer))
                        .with(JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)));

        verify(this.beerService, never()).updateBeer(beer.getId(), beer);
    }

    @Test
    void testBeerUpdateNullBeerStyle() throws Exception {
        final var beer = this.beerServiceImpl.getBeerPage(null, null, true, 1, 25).getContent().get(0);
        beer.setBeerStyle(null);

        this.mockMvc.perform(put(BeerController.BEER_PATH_ID, beer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(beer))
                        .with(JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)));

        verify(this.beerService, never()).updateBeer(beer.getId(), beer);
    }

    @Test
    void testBeerUpdateNullPrice() throws Exception {
        final var beer = this.beerServiceImpl.getBeerPage(null, null, true, 1, 25).getContent().get(0);
        beer.setPrice(null);

        this.mockMvc.perform(put(BeerController.BEER_PATH_ID, beer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(beer))
                        .with(JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)));

        verify(this.beerService, never()).updateBeer(beer.getId(), beer);
    }

    @Test
    void testBeerUpdateNullUpc() throws Exception {
        final var beer = this.beerServiceImpl.getBeerPage(null, null, true, 1, 25).getContent().get(0);
        beer.setUpc(null);

        this.mockMvc.perform(put(BeerController.BEER_PATH_ID, beer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(beer))
                        .with(JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(2)));

        verify(this.beerService, never()).updateBeer(beer.getId(), beer);
    }

    @Test
    void testBeerUpdateBlankUpc() throws Exception {
        final var beer = this.beerServiceImpl.getBeerPage(null, null, true, 1, 25).getContent().get(0);
        beer.setUpc("  ");

        this.mockMvc.perform(put(BeerController.BEER_PATH_ID, beer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(beer))
                        .with(JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)));

        verify(this.beerService, never()).updateBeer(beer.getId(), beer);
    }

    @Test
    void createBeer() throws Exception {
        final var beer = this.beerServiceImpl.getBeerPage(null, null, true, 1, 25).getContent().get(0);
        beer.setId(null);
        beer.setVersion(null);

        when(this.beerService.saveNewBeer(beer)).thenReturn(this.beerServiceImpl.getBeerPage(null, null, false, 1, 25).getContent().get(1));

        this.mockMvc.perform(post(BeerController.BEER_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(beer))
                        .with(JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().exists("Location"));
    }

    @Test
    void createBeerNullName() throws Exception {
        final var beer = this.beerServiceImpl.getBeerPage(null, null, true, 1, 25).getContent().get(0);
        beer.setId(null);
        beer.setVersion(null);
        beer.setBeerName(null);

        this.mockMvc.perform(post(BeerController.BEER_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(beer))
                        .with(JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(2)));
    }

    @Test
    void createBeerBlankName() throws Exception {
        final var beer = this.beerServiceImpl.getBeerPage(null, null, true, 1, 25).getContent().get(0);
        beer.setId(null);
        beer.setVersion(null);
        beer.setBeerName("  ");

        this.mockMvc.perform(post(BeerController.BEER_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(beer))
                        .with(JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    void createBeerNullBeerStyle() throws Exception {
        final var beer = this.beerServiceImpl.getBeerPage(null, null, true, 1, 25).getContent().get(0);
        beer.setId(null);
        beer.setVersion(null);
        beer.setBeerStyle(null);

        this.mockMvc.perform(post(BeerController.BEER_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(beer))
                        .with(JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    void createBeerNullPrice() throws Exception {
        final var beer = this.beerServiceImpl.getBeerPage(null, null, true, 1, 25).getContent().get(0);
        beer.setId(null);
        beer.setVersion(null);
        beer.setPrice(null);

        this.mockMvc.perform(post(BeerController.BEER_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(beer))
                        .with(JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    void createBeerNullUpc() throws Exception {
        final var beer = this.beerServiceImpl.getBeerPage(null, null, true, 1, 25).getContent().get(0);
        beer.setId(null);
        beer.setVersion(null);
        beer.setUpc(null);

        this.mockMvc.perform(post(BeerController.BEER_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(beer))
                        .with(JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(2)));
    }

    @Test
    void createBeerBlankUpc() throws Exception {
        final var beer = this.beerServiceImpl.getBeerPage(null, null, true, 1, 25).getContent().get(0);
        beer.setId(null);
        beer.setVersion(null);
        beer.setUpc("  ");

        this.mockMvc.perform(post(BeerController.BEER_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(beer))
                        .with(JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    void getBeerList() throws Exception {
        when(this.beerService.getBeerPage(null, null, null, null, null))
                .thenReturn(this.beerServiceImpl.getBeerPage(null, null, null, null, null));

        this.mockMvc.perform(get(BeerController.BEER_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()", is(3)));
    }

    @Test
    void getBeerById() throws Exception {
        final var testBeer = this.beerServiceImpl.getBeerPage(null, null, true, 1, 25).getContent().get(0);

        when(this.beerService.getBeerById(testBeer.getId())).thenReturn(Optional.of(testBeer));

        this.mockMvc.perform(get(BeerController.BEER_PATH_ID, testBeer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .with(JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testBeer.getId().toString())))
                .andExpect(jsonPath("$.beerName", is(testBeer.getBeerName())));

        Mockito.verify(this.beerService).getBeerById(any(UUID.class));
    }

    @Test
    void getBeerById_invalidCredentials() throws Exception {
        this.mockMvc.perform(get(BeerController.BEER_PATH_ID, UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}