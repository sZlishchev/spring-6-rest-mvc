package guru.springframework.spring6restmvc.controllers;

import guru.springframework.spring6restmvc.model.Beer;
import guru.springframework.spring6restmvc.services.BeerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Controller
public class BeerController {
    
    private final BeerService beerService;
    
    public Beer getBeerById(final UUID id) {
        log.info("Get beer by Id in Controller. Id: {}", id);
        
        return this.beerService.getBeer(id);
    }
}
