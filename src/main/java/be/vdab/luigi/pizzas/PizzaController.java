package be.vdab.luigi.pizzas;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Stream;

@RestController
@RequestMapping("pizzas")
class PizzaController {
    private final PizzaService pizzaService;
    PizzaController(PizzaService pizzaService) {
        this.pizzaService = pizzaService;
    }
    @GetMapping("aantal")
    long findAantal(){
        return pizzaService.findAantal();
    }

    //MET DTO
    private record IdNaamPrijs (long id, String naam, BigDecimal prijs){
        IdNaamPrijs (Pizza pizza){
            this(pizza.getId(), pizza.getNaam(), pizza.getPrijs());
        }
    }
    @GetMapping("{id}")
    IdNaamPrijs findById(@PathVariable long id){
        return pizzaService.findById(id)
                .map(pizza -> new IdNaamPrijs(pizza))
                .orElseThrow(()-> new PizzaNietGevondenException(id));
    }
    @GetMapping
    Stream<IdNaamPrijs> findAll(){
        return pizzaService.findAll()
                .stream()
                .map(pizza -> new IdNaamPrijs(pizza));
    }
    @GetMapping(params = "naamBevat")
    Stream<IdNaamPrijs> findByNaamBevat (String naamBevat) {
        return pizzaService.findByNaamBevat(naamBevat)
                .stream()
                .map(pizza -> new IdNaamPrijs(pizza));
    }
    @GetMapping(params = {"vanPrijs", "totPrijs"})
    Stream<IdNaamPrijs> findByPrijsTussen (BigDecimal vanPrijs, BigDecimal totPrijs){
        return pizzaService.findByPrijsTussen(vanPrijs, totPrijs)
                .stream()
                .map(pizza -> new IdNaamPrijs(pizza));
    }
    @DeleteMapping("{id}")
    void delete(@PathVariable long id){
        pizzaService.delete(id);
    }
    @PostMapping
    long create(@RequestBody @Valid NieuwePizza nieuwePizza){
        var id = pizzaService.create(nieuwePizza);
        return id;
    }
    @PatchMapping("{id}/prijs")
    void updatePris(@PathVariable long id, @RequestBody @NotNull @PositiveOrZero BigDecimal nieuwePrijs){
        var prijs = new Prijs(nieuwePrijs, LocalDateTime.now(), id);
        pizzaService.updatePrijs(prijs);
    }

    private record PrijsVanaf(BigDecimal prijs, LocalDateTime vanaf){
        PrijsVanaf(Prijs prijs){
            this(prijs.getPrijs(), prijs.getVanaf());
        }
    }
    @GetMapping("{id}/prijzen")
    Stream<PrijsVanaf> findPrijzen(@PathVariable long id){
        return pizzaService.findPrijzen(id)
                .stream()
                .map(prijs -> new PrijsVanaf(prijs));
    }





}
