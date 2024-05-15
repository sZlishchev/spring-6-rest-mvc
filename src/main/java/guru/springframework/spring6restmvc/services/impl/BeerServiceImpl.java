package guru.springframework.spring6restmvc.services.impl;

import guru.springframework.spring6restmvc.model.Beer;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.services.BeerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class BeerServiceImpl implements BeerService {
    
    private Map<UUID, Beer> beerMap;

    public BeerServiceImpl() {
        this.beerMap = new HashMap<>();
        
        final var lager = Beer.builder()
                .id(UUID.randomUUID())
                .beerName("Lager Classic")
                .beerStyle(BeerStyle.LAGER)
                .version(1)
                .upc("12324")
                .price(new BigDecimal("56.79"))
                .quantityOnHand(10)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        final var ale = Beer.builder()
                .id(UUID.randomUUID())
                .beerName("Ale Classic")
                .beerStyle(BeerStyle.ALE)
                .version(1)
                .upc("52462")
                .price(new BigDecimal("34.79"))
                .quantityOnHand(6)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        final var pilsner = Beer.builder()
                .id(UUID.randomUUID())
                .beerName("Pilsner Classic")
                .beerStyle(BeerStyle.PILSNER)
                .version(1)
                .upc("1532")
                .price(new BigDecimal("77.99"))
                .quantityOnHand(12)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();
        
        beerMap.put(lager.getId(), lager);
        beerMap.put(ale.getId(), ale);
        beerMap.put(pilsner.getId(), pilsner);
    }
    
    @Override
    public List<Beer> getBeerList() {
        return new ArrayList<>(this.beerMap.values());
    }

    @Override
    public Beer getBeer(UUID id) {
       return this.beerMap.get(id);
    }

    @Override
    public Beer saveNewBeer(Beer beer) {
        final var savedBeer = Beer.builder()
                .id(UUID.randomUUID())
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .beerName(beer.getBeerName())
                .beerStyle(beer.getBeerStyle())
                .quantityOnHand(beer.getQuantityOnHand())
                .price(beer.getPrice())
                .version(beer.getVersion())
                .upc(beer.getUpc())
                .build();
        
        this.beerMap.put(savedBeer.getId(), savedBeer);
        
        return savedBeer;
    }

    @Override
    public void updateBeer(UUID beerId, Beer beer) {
        final var beerToUpdate = this.beerMap.get(beerId);
        
        beerToUpdate.setBeerName(beer.getBeerName());
        beerToUpdate.setBeerStyle(beer.getBeerStyle());
        beerToUpdate.setUpc(beer.getUpc());
        beerToUpdate.setPrice(beer.getPrice());
        beerToUpdate.setQuantityOnHand(beer.getQuantityOnHand());
        beerToUpdate.setUpdatedDate(LocalDateTime.now());
    }

    @Override
    public void deleteBeerById(UUID beerId) {
        this.beerMap.remove(beerId);
    }

    @Override
    public void patchBeerById(UUID beerId, Beer beer) {
        final var beerToUpdate = this.beerMap.get(beerId);
        
        if(StringUtils.hasText(beer.getBeerName())) {
            beerToUpdate.setBeerName(beer.getBeerName());
        }
        
        if(beer.getBeerStyle() != null) {
            beerToUpdate.setBeerStyle(beer.getBeerStyle());
        }
        
        if(beer.getQuantityOnHand() != null) {
            beerToUpdate.setQuantityOnHand(beer.getQuantityOnHand());
        }
        
        if(beer.getPrice() != null) {
            beerToUpdate.setPrice(beer.getPrice());
        }
        
        if(StringUtils.hasText(beer.getUpc())) {
            beerToUpdate.setUpc(beer.getUpc());
        }
        
        beerToUpdate.setUpdatedDate(LocalDateTime.now());
    }
}
