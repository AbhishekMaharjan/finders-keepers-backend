package com.FindersKeepers.backend.controller;

import com.FindersKeepers.backend.projections.GetAllCountryProjection;
import com.FindersKeepers.backend.service.CountryService;
import com.nimbusds.jose.shaded.gson.Gson;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.TestPropertySources;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySources({
        @TestPropertySource(properties = "spring.liquibase.enabled=false")})
class CountryControllerTests {

    private final Gson gson = new Gson();

    @MockBean(name = "appSecurityFilterChain")
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CountryService countryService;
    @InjectMocks
    private CountryController countryController;

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    public void CountryController_GetAllCountry_ReturnListOfCountry() throws Exception {
        List<GetAllCountryProjection> getAllCountryProjectionList = new ArrayList<>();

        Mockito.when(countryService.getAllCountry(Mockito.any(String.class))).thenReturn(getAllCountryProjectionList);

        ResultActions response = mockMvc.perform(get("/country")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(getAllCountryProjectionList)));

        response.andExpect(status().is(200))
                .andDo(MockMvcResultHandlers.print());
    }
}
