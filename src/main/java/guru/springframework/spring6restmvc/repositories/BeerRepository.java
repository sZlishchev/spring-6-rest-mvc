package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.enteties.Beer;
import guru.springframework.spring6restmvc.model.BeerStyle;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BeerRepository extends JpaRepository<Beer, UUID> {
    
    List<Beer> findAllByBeerNameIsLikeIgnoreCase(final String beerName);
    
    List<Beer> findAllByBeerStyle( final BeerStyle beerStyle);

    List<Beer> findAllByBeerNameIsLikeIgnoreCaseAndBeerStyle(final String beerName, final BeerStyle beerStyle);
}
