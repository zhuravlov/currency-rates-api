package com.zhuravlov.currencyratesapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhuravlov.currencyratesapi.dto.CurrencyDto;
import com.zhuravlov.currencyratesapi.repository.ExchangeRateRepository;
import com.zhuravlov.currencyratesapi.repository.ObservableCurrencyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@ContextConfiguration(classes = IntegrationTestConfiguration.class)
@Sql(value = "/db/test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class IntegrationTest {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObservableCurrencyRepository currencyRepo;

    @Autowired
    private ExchangeRateRepository rateRepository;

    @Test
    void testGetAllObservableCurrencies() throws Exception {
        mockMvc.perform(get("/api/currencies"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$[0].code", is("EUR")))
                .andExpect(jsonPath("$[1].code", is("USD")));
    }

    @Test
    void testLoadingRatesAfterStartApplication() throws Exception {

        System.out.println(currencyRepo.findAll());
        System.out.println(rateRepository.findAll());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/currencies/{baseCurrency}/rates", "USD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.base", is("USD")))
                .andExpect(jsonPath("$.timestamp", greaterThan(0)))
                .andExpect(jsonPath("$.rates.EUR", greaterThan(0.0)));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/currencies/{baseCurrency}/rates", "EUR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.base", is("EUR")))
                .andExpect(jsonPath("$.timestamp", greaterThan(0)))
                .andExpect(jsonPath("$.rates.USD", greaterThan(0.0)));
    }

    @Test
    void testAddNewObservableCurrency() throws Exception {
        var gbr = new CurrencyDto("PLN");
        String stringJson = objectMapper.writeValueAsString(gbr);
        mockMvc.perform(post("/api/currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stringJson))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/currencies"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$[2].code", is("PLN")));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/currencies/{baseCurrency}/rates", "PLN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.base", is("PLN")))
                .andExpect(jsonPath("$.timestamp", greaterThan((int)LocalDateTime.now().minusDays(2).toEpochSecond(ZoneOffset.UTC))))
                .andExpect(jsonPath("$.rates.EUR", greaterThan(3.0)));
    }
}
