package be.vdab.luigi.pizzas;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@Transactional
@Sql("/pizzas.sql")
@AutoConfigureMockMvc
class PizzaControllerTest {
    private final static String PIZZAS_TABLE = "pizzas";
    private final MockMvc mockMvc;
    private final JdbcClient jdbcClient;
    private final static Path TEST_RESOURCES = Path.of("src/test/resources");

    PizzaControllerTest(MockMvc mockMvc, JdbcClient jdbcClient) {
        this.mockMvc = mockMvc;
        this.jdbcClient = jdbcClient; }
    @Test
    void findAantalVindtHetJuisteAantalPizzas() throws Exception {
        mockMvc.perform(get("/pizzas/aantal"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$")
                                .value(JdbcTestUtils.countRowsInTable(jdbcClient, PIZZAS_TABLE)));
    }
    private long idVanTest1Pizza(){
        return jdbcClient.sql("select id from pizzas where naam = 'test1'")
                .query(long.class)
                .single();
    }
    @Test
    void findByIdMetEenBestaandeIdVindtDePizza() throws Exception{
        var id = idVanTest1Pizza();
        mockMvc.perform(get("/pizzas/{id}", id))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("id").value(id),
                        jsonPath("naam").value("test1")
                );

    }
    @Test
    void findByIdMetEenOnbestaandeIdGeeftNotFound() throws Exception{
        mockMvc.perform(get("/pizzas/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }
    @Test
    void findAllVindtAllePizzas() throws Exception{
        mockMvc.perform(get("/pizzas"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("length()")
                                .value(JdbcTestUtils.countRowsInTable(jdbcClient, PIZZAS_TABLE))
                );
    }
    @Test
    void findByNaamBevatVindtDeJuistePizzas() throws Exception{
        mockMvc.perform(get("/pizzas")
                .param("naamBevat", "test"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("length()").value(JdbcTestUtils.countRowsInTableWhere(
                                jdbcClient, PIZZAS_TABLE, "naam like '%test%'")));

    }
    @Test
    void findByPrijsTussenVindtDeJuistePizzas() throws Exception{
        mockMvc.perform(get("/pizzas")
                .param("vanPrijs", "10")
                .param("totPrijs", "20"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("length()").value(JdbcTestUtils.countRowsInTableWhere(
                                jdbcClient, PIZZAS_TABLE,"prijs between 10 and 20"))

                );
    }
    @Test
    void deleteVerwijdertDePizza() throws Exception{
        var id= idVanTest1Pizza();
        mockMvc.perform(delete("/pizzas/{id}", id))
                .andExpect(status().isOk());
        assertThat(JdbcTestUtils.countRowsInTableWhere(jdbcClient, PIZZAS_TABLE, "id=" + id)).isZero();
    }

    @Test
    void createVoegtPizzaToe() throws Exception{
        var jsonData = Files.readString(TEST_RESOURCES.resolve("correctePizza.json"));
        var responseBody = mockMvc.perform(post("/pizzas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonData))
        .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(JdbcTestUtils.countRowsInTableWhere(
                jdbcClient, PIZZAS_TABLE, "naam= 'test3' and id=" + responseBody)).isOne();
    }

}
