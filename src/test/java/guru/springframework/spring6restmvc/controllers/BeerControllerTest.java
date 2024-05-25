package guru.springframework.spring6restmvc.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeerController.class)
class BeerControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private BeerService beerService;
    
    @Captor
    private ArgumentCaptor<UUID> uuidCaptor;
    
    @Captor ArgumentCaptor<BeerDTO> beerCaptor;
    
    private final BeerServiceImpl beerServiceImpl = new BeerServiceImpl();
    
    @Test
    void testGetBeerByIdNotFound() throws Exception {
        
        when(this.beerService.getBeerById(any(UUID.class))).thenReturn(Optional.empty());
        
        this.mockMvc.perform(get(BeerController.BEER_PATH_ID, UUID.randomUUID()))
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
                .content(this.objectMapper.writeValueAsString(beer)))
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
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        verify(this.beerService).deleteBeerById(beerId);
    }
    
    @Test
    void testBeerUpdate() throws Exception {
        final var beer = this.beerServiceImpl.getBeerList().get(0);
        
        when(this.beerService.updateBeer(beer.getId(), beer))
            .thenReturn(Optional.ofNullable(BeerDTO.builder().build()));
        
        this.mockMvc.perform(put(BeerController.BEER_PATH_ID, beer.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(beer)))
                .andExpect(status().isOk());
        
        verify(this.beerService).updateBeer(beer.getId(), beer);
    }
    
    @Test
    void createBeer() throws Exception {
        final var beer = this.beerServiceImpl.getBeerList().get(0);
        beer.setId(null);
        beer.setVersion(null);
        
        when(this.beerService.saveNewBeer(beer)).thenReturn(this.beerServiceImpl.getBeerList().get(1));
        
        this.mockMvc.perform(post(BeerController.BEER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(beer)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().exists("Location"));
    }
    
    @Test
    void getBeerList() throws Exception {
        when(this.beerService.getBeerList()).thenReturn(this.beerServiceImpl.getBeerList());
        
        this.mockMvc.perform(get(BeerController.BEER_PATH)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(3)));
    }
     
    @Test
    void getBeerById() throws Exception {
        final var testBeer = this.beerServiceImpl.getBeerList().get(0);
        
        when(this.beerService.getBeerById(testBeer.getId())).thenReturn(Optional.of(testBeer));

        this.mockMvc.perform(get(BeerController.BEER_PATH_ID, testBeer.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testBeer.getId().toString())))
                .andExpect(jsonPath("$.beerName", is(testBeer.getBeerName())));
        
        Mockito.verify(this.beerService).getBeerById(any(UUID.class));
               
    }
}