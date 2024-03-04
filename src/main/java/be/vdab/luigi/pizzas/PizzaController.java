package be.vdab.luigi.pizzas;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
class PizzaController {
    private final PizzaService pizzaService;
    PizzaController(PizzaService pizzaService) {
        this.pizzaService = pizzaService;
    }
    @GetMapping("pizzas/aantal")
    long findAantal(){
        return pizzaService.findAantal();
    }

    //MET DTO
    private record IdNaamPrijs (long id, String naam, BigDecimal prijs){
        IdNaamPrijs (Pizza pizza){
            this(pizza.getId(), pizza.getNaam(), pizza.getPrijs());
        }
    }
    @GetMapping("pizzas/{id}")
    IdNaamPrijs findById(@PathVariable long id){
        return pizzaService.findById(id)
                .map(pizza -> new IdNaamPrijs(pizza))
                .orElseThrow(()-> new PizzaNietGevondenException(id));
    }







}
