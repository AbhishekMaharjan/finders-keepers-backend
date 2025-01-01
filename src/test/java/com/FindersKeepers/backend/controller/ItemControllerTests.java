package com.FindersKeepers.backend.controller;

import com.FindersKeepers.backend.initializer.model.ItemInitializer;
import com.FindersKeepers.backend.initializer.pojo.ItemPojoInitializer;
import com.FindersKeepers.backend.model.Item;
import com.FindersKeepers.backend.pojo.model.ItemPojo;
import com.FindersKeepers.backend.service.ItemService;
import com.nimbusds.jose.shaded.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.TestPropertySources;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySources({
        @TestPropertySource(properties = "spring.liquibase.enabled=false")})
class ItemControllerTests {

    private final Gson gson = new Gson();

    @MockBean(name = "appSecurityFilterChain")
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    private ItemInitializer itemInitializer = new ItemInitializer();
    private ItemPojoInitializer itemPojoInitializer = new ItemPojoInitializer();

    @BeforeEach
    public void init() {
        itemInitializer = new ItemInitializer();
        itemPojoInitializer = new ItemPojoInitializer();
    }

    @Test
    void save_ReturnsCreatedResponse() throws Exception {
        Item item = itemInitializer.getFoundItem();
        ItemPojo itemPojo = itemPojoInitializer.getItemPojo();
        Mockito.when(itemService.save(Mockito.any(ItemPojo.class))).thenReturn(item);

        ResultActions response = mockMvc.perform(post("/item/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(itemPojo)));

        response.andExpect(status().isCreated())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void getAllItems_ReturnsListOfItems() throws Exception {
        Mockito.when(itemService.findAllItems(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.any()))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        ResultActions response = mockMvc.perform(get("/item")
                .param("filterItemByStatus", "LOST")
                .param("itemCategoryId", "")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void getBestMatchItems_ReturnsBestMatchFoundItems() throws Exception {
        Mockito.when(itemService.findBestMatchItemsByItemId(Mockito.any(Long.class))).thenReturn(Collections.emptyList());

        ResultActions response = mockMvc.perform(get("/item/best-match/{lostItemId}", "1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}
