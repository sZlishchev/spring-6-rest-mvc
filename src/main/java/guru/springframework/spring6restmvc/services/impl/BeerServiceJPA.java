package guru.springframework.spring6restmvc.services.impl;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import guru.springframework.spring6restmvc.enteties.Beer;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import guru.springframework.spring6restmvc.services.BeerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Primary
@RequiredArgsConstructor
public class BeerServiceJPA implements BeerService {
  
  private static final int DEFAULT_PAGE = 0;

  private static final int DEFAULT_PAGE_SIZE = 25;
  
  private final BeerRepository beerRepository;
  private final BeerMapper beerMapper;

  @Override
  public Page<BeerDTO> getBeerPage(String beerName, BeerStyle beerStyle, Boolean showInventory, Integer pageNumber, Integer pageSize) {
    final Page<Beer> beerPage;

    final var pageRequest = this.buildPageRequest(pageNumber, pageSize);

    if (StringUtils.hasText(beerName) && beerStyle == null) {
      beerPage = this.getBeerListByName(beerName, pageRequest);
    } else if (!StringUtils.hasText(beerName) && beerStyle != null) {
      beerPage = this.getBeerListByStyle(beerStyle, pageRequest);
    } else if (StringUtils.hasText(beerName) && beerStyle != null) {
      beerPage = this.getBeerListByNameAndStyle(beerName, beerStyle, pageRequest);
    } else {
      beerPage = this.beerRepository.findAll(pageRequest);
    }
    
    if (Objects.nonNull(showInventory) && !showInventory) {
      beerPage.forEach(beer -> beer.setQuantityOnHand(null));
    }
    
    return beerPage.map(this.beerMapper::beerToBeerDTO);
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

  private Page<Beer> getBeerListByNameAndStyle(final String beerName, final BeerStyle beerStyle, final Pageable pageable) {
    return this.beerRepository.findAllByBeerNameIsLikeIgnoreCaseAndBeerStyle("%" + beerName + "%", beerStyle, pageable);
  }

  private Page<Beer> getBeerListByStyle(final BeerStyle beerStyle, final Pageable pageable) {
    return this.beerRepository.findAllByBeerStyle(beerStyle, pageable);
  }

  private Page<Beer> getBeerListByName(final String beerName, final Pageable pageable) {
    return this.beerRepository.findAllByBeerNameIsLikeIgnoreCase("%" + beerName + "%", pageable);
  }
  
  private PageRequest buildPageRequest(final Integer pageNumber, final Integer pageSize) {
    final int queryPageNumber;
    final int queryPageSize;
    
    if (Objects.nonNull(pageNumber) && pageNumber > DEFAULT_PAGE) {
      queryPageNumber = pageNumber;
    } else {
      queryPageNumber = DEFAULT_PAGE;
    }
    
    if (Objects.nonNull(pageSize) && pageSize > DEFAULT_PAGE_SIZE) {
      if (pageSize > 1000) {
        queryPageSize = 1000;
      } else {
        queryPageSize = pageSize;
      }
    } else {
      queryPageSize = DEFAULT_PAGE_SIZE;
    }
    
    final var sort = Sort.by(Sort.Order.asc("beerName"));
    
    return PageRequest.of(queryPageNumber, queryPageSize, sort);
  }
}
