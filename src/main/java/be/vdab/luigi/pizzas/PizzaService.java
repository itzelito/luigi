package be.vdab.luigi.pizzas;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
class PizzaService {
    private final PizzaRepository pizzaRepository;
    public PizzaService(PizzaRepository pizzaRepository) {
        this.pizzaRepository = pizzaRepository;
    }
    long findAantal() {
        return pizzaRepository.findAantal();
    }
    Optional<Pizza> findById(long id){
        return pizzaRepository.findById(id);
    }
    List<Pizza> findAll(){
        return pizzaRepository.findAll();
    }
    List<Pizza> findByNaamBevat (String woord){
        return pizzaRepository.findByNaamBevat(woord);
    }
    List<Pizza> findByPrijsTussen (BigDecimal van, BigDecimal tot){
        return pizzaRepository.findPrijsTussen(van, tot);
    }
    @Transactional
    void delete (long id){
        pizzaRepository.delete(id);
    }




}
