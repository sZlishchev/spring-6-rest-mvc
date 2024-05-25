package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.BeerDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BeerService {
    List<BeerDTO> getBeerList();

    Optional<BeerDTO> getBeerById(final UUID id);

    BeerDTO saveNewBeer(final BeerDTO beer);

    Optional<BeerDTO> updateBeer(UUID beerId, BeerDTO beer);

    Boolean deleteBeerById(UUID beerId);

    Optional<BeerDTO> patchBeerById(UUID beerId, BeerDTO beer);
}
