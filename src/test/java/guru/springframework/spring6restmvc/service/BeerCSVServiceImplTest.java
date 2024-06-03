package guru.springframework.spring6restmvc.service;

import guru.springframework.spring6restmvc.services.BeerCSVService;
import guru.springframework.spring6restmvc.services.impl.BeerCSVServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;


public class BeerCSVServiceImplTest {
    private BeerCSVService beerCSVService = new BeerCSVServiceImpl();

    @Test
    void testConvertCSV() throws FileNotFoundException {
        final var file = ResourceUtils.getFile("classpath:csvdata/beer.csv");
        
        final var records = this.beerCSVService.convertCSV(file);

        System.out.println(records.size());
        
        assertThat(records.size()).isGreaterThan(0);
    }
}
