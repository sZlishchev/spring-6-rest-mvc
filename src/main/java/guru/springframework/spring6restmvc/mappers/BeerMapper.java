package guru.springframework.spring6restmvc.mappers;

import java.util.List;

import guru.springframework.spring6restmvc.enteties.Beer;
import guru.springframework.spring6restmvc.model.BeerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface BeerMapper {
  
  Beer beerDTOtoBeer(final BeerDTO beerDTO);
  
  List<BeerDTO> beerListToBeerListDto(final List<Beer> beerList);
  
  BeerDTO beerToBeerDTO(final Beer beer);

}
