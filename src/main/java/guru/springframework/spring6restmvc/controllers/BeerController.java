package guru.springframework.spring6restmvc.controllers;

import guru.springframework.spring6restmvc.exception.NotFoundException;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.services.BeerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class BeerController {
    
    public static final String BEER_PATH = "/api/v1/beer";
    public static final String BEER_PATH_ID = BEER_PATH + "/{beerId}";
    
    private final BeerService beerService;
    
    @PatchMapping(BEER_PATH_ID)
    public ResponseEntity<BeerDTO> patchBeer(@PathVariable UUID beerId, @RequestBody BeerDTO beer) {
        this.beerService.patchBeerById(beerId, beer);
        
        
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @DeleteMapping(BEER_PATH_ID)
    public ResponseEntity<BeerDTO> deleteBeer(@PathVariable UUID beerId) {
        this.beerService.deleteBeerById(beerId);
        
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @PutMapping(BEER_PATH_ID)
    public ResponseEntity<BeerDTO> updateBeer(@PathVariable UUID beerId, @RequestBody BeerDTO beer) {
        
        this.beerService.updateBeer(beerId, beer);
        
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @PostMapping(BEER_PATH)
    public ResponseEntity<BeerDTO> saveNewBeer(@RequestBody BeerDTO beer) {
        final var savedBeer = this.beerService.saveNewBeer(beer);
        
        final var headers = new HttpHeaders();
        
        headers.add("Location", "/api/v1/beer/" + savedBeer.getId());
        
        return new ResponseEntity<>(savedBeer, headers ,HttpStatus.CREATED);
    }
    
    @GetMapping(BEER_PATH)
    public List<BeerDTO> getBeerList() {
        return beerService.getBeerList();
    }
    
    @GetMapping(BEER_PATH_ID)
    public BeerDTO getBeerById(@PathVariable final UUID beerId) {
        log.info("Get beer by Id in Controller. Id: {} modified", beerId);
        
        return this.beerService.getBeerById(beerId).orElseThrow(NotFoundException::new);
    }
}
