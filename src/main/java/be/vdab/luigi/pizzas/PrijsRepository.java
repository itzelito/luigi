package be.vdab.luigi.pizzas;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
class PrijsRepository {
    private final JdbcClient jdbcClient;

    public PrijsRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    void create(Prijs prijs){
        var sql = """
                insert into prijzen (prijs, vanaf, pizzaId)
                values (?, ?, ?)
                """;
        jdbcClient.sql(sql)
                .params(prijs.getPrijs(), prijs.getVanaf(), prijs.getPizzaId())
                .update();
    }

}
