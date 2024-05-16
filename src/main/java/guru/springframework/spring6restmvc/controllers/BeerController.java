package guru.springframework.spring6restmvc.controllers;

import guru.springframework.spring6restmvc.model.Beer;
import guru.springframework.spring6restmvc.services.BeerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/beer")
public class BeerController {
    
    private final BeerService beerService;
    
    @PatchMapping("{beerId}")
    public ResponseEntity<Beer> patchBeer(@PathVariable UUID beerId, @RequestBody Beer beer) {
        this.beerService.patchBeerById(beerId, beer);
        
        
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @DeleteMapping("{beerId}")
    public ResponseEntity<Beer> deleteBeer(@PathVariable UUID beerId) {
        this.beerService.deleteBeerById(beerId);
        
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @PutMapping("{beerId}")
    public ResponseEntity<Beer> updateBeer(@PathVariable UUID beerId, @RequestBody Beer beer) {
        
        this.beerService.updateBeer(beerId, beer);
        
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<Beer> saveNewBeer(@RequestBody Beer beer) {
        final var savedBeer = this.beerService.saveNewBeer(beer);
        
        final var headers = new HttpHeaders();
        
        headers.add("Location", "/api/v1/beer/" + savedBeer.getId());
        
        return new ResponseEntity<>(savedBeer, headers ,HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public List<Beer> getBeerList() {
        return beerService.getBeerList();
    }

    @RequestMapping(method = RequestMethod.GET, path = "{beerId}")
    public Beer getBeerById(@PathVariable final UUID beerId) {
        log.info("Get beer by Id in Controller. Id: {} modified", beerId);
        
        return this.beerService.getBeer(beerId);
    }
}
