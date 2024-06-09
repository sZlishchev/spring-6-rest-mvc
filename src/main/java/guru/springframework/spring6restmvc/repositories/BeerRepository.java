package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.enteties.Beer;
import guru.springframework.spring6restmvc.model.BeerStyle;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BeerRepository extends JpaRepository<Beer, UUID> {
    
    Page<Beer> findAllByBeerNameIsLikeIgnoreCase(final String beerName, final Pageable pageable);
    
    Page<Beer> findAllByBeerStyle( final BeerStyle beerStyle, final Pageable pageable);

    Page<Beer> findAllByBeerNameIsLikeIgnoreCaseAndBeerStyle(final String beerName, final BeerStyle beerStyle, final Pageable pageable);
}
