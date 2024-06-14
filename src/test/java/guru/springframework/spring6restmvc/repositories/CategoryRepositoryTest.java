package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.enteties.Beer;
import guru.springframework.spring6restmvc.enteties.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CategoryRepositoryTest {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private BeerRepository beerRepository;
    
    private Beer testBeer;

    @BeforeEach
    void setUp() {
        testBeer = this.beerRepository.findAll().get(0);
    }
    
    @Transactional
    @Test
    void testSaveCategory() {
        final var savedCategory = this.categoryRepository.save(Category.builder()
                .description("Ales")
                .build());
        
        testBeer.addCategory(savedCategory);
        
        final var savedBeer = this.beerRepository.save(testBeer);
        
        assertNotNull(savedBeer);
        assertFalse(savedBeer.getCategories().isEmpty());
        assertTrue(savedBeer.getCategories().contains(savedCategory));
    }
}