package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.enteties.BeerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BeerOrderRepository extends JpaRepository<BeerOrder, UUID> {
}
