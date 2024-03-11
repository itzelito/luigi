package be.vdab.luigi.pizzas;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
class PizzaRepository {
    private final JdbcClient jdbcClient;
    public PizzaRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }
    long findAantal(){
        var sql = """
                select count(*)
                as aantalPizzas
                from pizzas
                """;
        return jdbcClient.sql(sql)
                .query(Long.class)
                .single();
    }
    Optional<Pizza> findById(long id) {
        var sql = """
                select id, naam, prijs, winst
                from pizzas
                where id = ?
                """;
        return jdbcClient.sql(sql)
                .param(id)
                .query(Pizza.class)
                .optional();
    }
    List<Pizza> findAll(){
        var sql = """
                select id, naam, prijs, winst
                from pizzas
                order by naam
                """;
               return jdbcClient.sql(sql)
                       .query(Pizza.class)
                       .list();
    }
    List<Pizza> findByNaamBevat (String woord){
        var sql = """
                select id, naam, prijs, winst
                from pizzas
                where naam like ?
                order by naam
                """;
        return jdbcClient.sql(sql)
                .param("%" + woord + "%")
                .query(Pizza.class)
                .list();
    }

    List<Pizza> findPrijsTussen(BigDecimal van, BigDecimal tot){
        var sql= """
                select id, naam, prijs, winst
                from pizzas
                where prijs between ? and ? 
                order by prijs
                """;
        return jdbcClient.sql(sql)
                .params(van, tot)
                .query(Pizza.class)
                .list();
    }
    void delete(long id){
        var sql= """
                delete from pizzas
                where id = ?
                """;
        jdbcClient.sql(sql)
                .param(id)
                .update();
    }
    long create(Pizza pizza){
        var sql = """
                insert into pizzas (naam, prijs, winst)
                values (?, ?, ?)
                """;
        var keyholder = new GeneratedKeyHolder();
        jdbcClient.sql(sql)
                .params(pizza.getNaam(), pizza.getPrijs(), pizza.getWinst())
                .update(keyholder);
        return keyholder.getKey().longValue();

    }
    void updatePrijs(long id, BigDecimal prijs){
        var sql= """
                update pizzas
                set prijs = ?
                where id = ?
                """;
       if(jdbcClient.sql(sql).params(prijs, id).update()== 0){
           throw new PizzaNietGevondenException(id);
       }

    }


}
