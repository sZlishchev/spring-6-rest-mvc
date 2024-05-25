package guru.springframework.spring6restmvc.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import guru.springframework.spring6restmvc.services.BeerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Primary
@RequiredArgsConstructor
public class BeerServiceJPA implements BeerService {
  
  private final BeerRepository beerRepository;
  private final BeerMapper beerMapper;

  @Override
  public List<BeerDTO> getBeerList() {
    return this.beerMapper.beerListToBeerListDto(this.beerRepository.findAll());
  }

  @Override
  public Optional<BeerDTO> getBeerById(UUID id) {
    return Optional.ofNullable(this.beerMapper.beerToBeerDTO(this.beerRepository.findById(id)
        .orElse(null)));
  }

  @Override
  public BeerDTO saveNewBeer(BeerDTO beer) {
    return this.beerMapper.beerToBeerDTO(this.beerRepository.save(this.beerMapper.beerDTOtoBeer(beer)));
  }

  @Override
  public Optional<BeerDTO> updateBeer(UUID beerId, BeerDTO beer) {
    final var atomicReference = new AtomicReference<Optional<BeerDTO>>();
    
    this.beerRepository.findById(beerId).ifPresentOrElse( beerToUpdate -> {
      beerToUpdate.setBeerName(beer.getBeerName());
      beerToUpdate.setBeerStyle(beer.getBeerStyle());
      beerToUpdate.setPrice(beer.getPrice());
      beerToUpdate.setUpc(beer.getUpc());
      beerToUpdate.setQuantityOnHand(beer.getQuantityOnHand());
      beerToUpdate.setUpdatedDate(LocalDateTime.now());
        
      atomicReference.set(Optional.of(this.beerMapper.beerToBeerDTO(this.beerRepository.save(beerToUpdate))));
    }, () -> {
      atomicReference.set(Optional.empty());
    });
    
    return atomicReference.get();
  }

  @Override
  public Boolean deleteBeerById(UUID beerId) {
      if(this.beerRepository.existsById(beerId)) {
        this.beerRepository.deleteById(beerId);
        
        return true;
      }
      
      return false;
  }

  @Override
  public Optional<BeerDTO> patchBeerById(UUID beerId, BeerDTO beer) {
    final var atomicReference = new AtomicReference<Optional<BeerDTO>>();
    
    
    this.beerRepository.findById(beerId)
            .ifPresentOrElse(beerToUpdate -> {
              
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

    atomicReference.set(Optional.of(this.beerMapper.beerToBeerDTO(this.beerRepository.save(beerToUpdate))));
    }, () -> atomicReference.set(Optional.empty()));
  
    return atomicReference.get();
  }
}
