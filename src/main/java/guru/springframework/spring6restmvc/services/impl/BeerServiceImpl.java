package guru.springframework.spring6restmvc.services.impl;

import guru.springframework.spring6restmvc.model.Beer;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.services.BeerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class BeerServiceImpl implements BeerService {
    @Override
    public Beer getBeer(UUID id) {
        log.info("Get beer by Id in service. Id: {}", id);
        return Beer.builder()
                .id(id)
                .beerName("BeerName")
                .beerStyle(BeerStyle.LAGER)
                .version(1)
                .upc("12324")
                .price(new BigDecimal("56.79"))
                .quantityOnHand(10)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();
    }
}
