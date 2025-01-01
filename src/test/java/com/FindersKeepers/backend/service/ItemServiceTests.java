package com.FindersKeepers.backend.service;

import com.FindersKeepers.backend.config.resources.CustomMessageSource;
import com.FindersKeepers.backend.enums.item.ItemStatus;
import com.FindersKeepers.backend.exception.NotFoundException;
import com.FindersKeepers.backend.initializer.model.ItemInitializer;
import com.FindersKeepers.backend.initializer.pojo.ItemPojoInitializer;
import com.FindersKeepers.backend.model.Item;
import com.FindersKeepers.backend.model.ItemCategory;
import com.FindersKeepers.backend.pojo.model.ItemPojo;
import com.FindersKeepers.backend.repository.ItemRepository;
import com.FindersKeepers.backend.service.impl.ItemServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = "spring.liquibase.enabled=false")
public class ItemServiceTests {

    @MockBean
    private SecurityFilterChain securityFilterChain;

    @Mock
    private CustomMessageSource customMessageSource;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private AddressService addressService;

    @Mock
    private FileService fileService;

    @Mock
    private UserService userService;

    @Mock
    private ItemCategoryService itemCategoryService;

    @Mock
    private DescriptiveAttributesService descriptiveAttributesService;

    @InjectMocks
    private ItemServiceImpl itemService;

    private ItemInitializer itemInitializer;
    private ItemPojoInitializer itemPojoInitializer;

    @BeforeEach
    public void init() {
        itemInitializer = new ItemInitializer();
        itemPojoInitializer = new ItemPojoInitializer();
        ItemCategory category = new ItemCategory();
        category.setLevel(1);
    }


    @Test
    void ItemService_CheckIt_ReturnsItem() {
        Item mockItem = itemInitializer.getFoundItem();
        when(itemRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(mockItem));

        Item item = itemService.findById(1L);

        Assertions.assertThat(item).isNotNull();
    }


    @Test
    void ItemService_CheckItWhenWrongIdProvided_ReturnsNotFoundError() {
        when(customMessageSource.get("item"))
                .thenReturn("Item");

        when(customMessageSource.get("error.id.not.found", customMessageSource.get("item")))
                .thenThrow(new NotFoundException("Item id not found"));

        try {
            itemService.findById(100L);
        } catch (Exception ex) {
            assertAll(
                    () -> Assertions.assertThat(ex.getMessage()).isEqualTo("Item id not found"),
                    () -> Assertions.assertThat(ex.getClass().getName()).isEqualTo(NotFoundException.class.getName())
            );
        }
    }


    @Test
    void ItemService_SaveItem_ReturnsSavedItem() {
        Item mockItem = itemInitializer.getFoundItem();

        ItemPojo itemPojo = itemPojoInitializer.getItemPojo();

        when(itemRepository.save(Mockito.any(Item.class))).thenReturn(mockItem);

        Item saved = itemService.save(itemPojo);

        Assertions.assertThat(saved).isNotNull();
        Assertions.assertThat(saved).isEqualTo(mockItem);
    }

    @Test
    void ItemService_FindAllItems_ReturnsPagedResults() {

        Page<Item> mockPage = new PageImpl<>(new ArrayList<>());
        when(itemRepository.findAllByItemStatus(Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenReturn(mockPage);

        Page<Item> result = itemService.findAllItems("FOUND", 0, 10, null);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(mockPage);
    }

    @Test
    void ItemService_FindAllItemsWithCategory_ReturnsPagedResults() {
        ItemCategory mockCategory = new ItemCategory();
        Page<Item> mockPage = new PageImpl<>(new ArrayList<>());

        when(itemCategoryService.findById(Mockito.anyLong())).thenReturn(mockCategory);
        when(itemRepository.findAllByItemStatusAndItemCategoryListContains(Mockito.anyString(), Mockito.eq(mockCategory), Mockito.any(Pageable.class)))
                .thenReturn(mockPage);

        Page<Item> result = itemService.findAllItems("FOUND", 0, 10, 1L);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(mockPage);
    }

    @Test
    void ItemService_ChangeItemStatusToClaimed_UpdatesStatus() {
        Item mockItem = itemInitializer.getFoundItem();
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mockItem));
        when(itemRepository.save(Mockito.any(Item.class))).thenReturn(mockItem);

        itemService.changeItemStatus(1L);

        Assertions.assertThat(mockItem.getItemStatus()).isEqualTo(ItemStatus.CLAIMED.name());
    }

    @Test
    void ItemService_FindBestMatchItems_ReturnsMatchedItems() {

        Item lostItem = itemInitializer.getLostItem();
        Item foundItem = itemInitializer.getFoundItem();
        ItemCategory category = itemInitializer.getFoundItem().getItemCategoryList().get(1); //actual matching category get

        when(itemRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(lostItem));
        when(itemCategoryService.findById(Mockito.anyLong())).thenReturn(category);
        when(itemRepository.findAllByItemStatusAndItemCategoryListContains(
                Mockito.anyString(),
                Mockito.any(ItemCategory.class),
                Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(List.of(foundItem)));

        List<Item> matches = itemService.findBestMatchItemsByItemId(1L);
        Assertions.assertThat(matches).isNotEmpty();
        Assertions.assertThat(matches.get(0)).isEqualTo(foundItem);
    }


    @Test
    void ItemService_FindBestMatchItems_NoMatchesFound_ReturnsEmptyList() {
        Item lostItem = itemInitializer.getLostItem();
        Item foundItem = itemInitializer.getFoundItem();
        ItemCategory category = itemInitializer.getFoundItem().getItemCategoryList().get(0);  //parent category get which is not matching

        when(itemRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(lostItem));
        when(itemCategoryService.findById(Mockito.anyLong())).thenReturn(category);
        when(itemRepository.findAllByItemStatusAndItemCategoryListContains(
                Mockito.anyString(),
                Mockito.any(ItemCategory.class),
                Mockito.any(Pageable.class)))
                .thenReturn(Page.empty());

        List<Item> matches = itemService.findBestMatchItemsByItemId(1L);

        Assertions.assertThat(matches).isEmpty();
    }

}
