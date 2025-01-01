package com.FindersKeepers.backend.service.impl;

import com.FindersKeepers.backend.config.resources.CustomMessageSource;
import com.FindersKeepers.backend.enums.item.ItemStatus;
import com.FindersKeepers.backend.exception.NotFoundException;
import com.FindersKeepers.backend.model.Item;
import com.FindersKeepers.backend.model.ItemCategory;
import com.FindersKeepers.backend.pojo.model.ItemPojo;
import com.FindersKeepers.backend.repository.ItemRepository;
import com.FindersKeepers.backend.service.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.FuzzyScore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.FindersKeepers.backend.constant.message.ErrorConstantValue.ERROR_ID_NOT_FOUND;
import static com.FindersKeepers.backend.constant.message.FieldConstantValue.ELECTRONICS_CATEGORY;
import static com.FindersKeepers.backend.constant.message.FieldConstantValue.ITEM;
import static com.FindersKeepers.backend.enums.item.ItemStatus.FOUND;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CustomMessageSource customMessageSource;
    private final UserService userService;
    private final AddressService addressService;
    private final DescriptiveAttributesService descriptiveAttributesService;
    private final ItemCategoryService itemCategoryService;
    private final FileService fileService;

    /**
     * Processes an item's descriptive attributes into a normalized string.
     * Removes any special characters from the brand and color attributes
     * and combines them into a single string.
     *
     * @param item The item whose descriptive attributes will be processed.
     * @return A normalized string combining the item's brand and color, or an empty string if null.
     */
    private static String getProcessedAttributes(Item item) {
        String lostItemAttributes = "";
        if (Objects.nonNull(item.getDescriptiveAttributes()) && Objects.nonNull(item.getDescriptiveAttributes().getBrand()) && Objects.nonNull(item.getDescriptiveAttributes().getColor())) {
            String processedBrand = item.getDescriptiveAttributes().getBrand().replaceAll("[^a-zA-Z0-9\\s]", "");
            String processedColor = item.getDescriptiveAttributes().getColor().replaceAll("[^a-zA-Z0-9\\s]", "");
            lostItemAttributes = processedBrand + " " + processedColor;
        }
        return lostItemAttributes;
    }

    public Item findById(Long id) {
        return itemRepository.findById(id).orElseThrow(
                () -> new NotFoundException(customMessageSource.get(ERROR_ID_NOT_FOUND, customMessageSource.get(ITEM)))
        );
    }

    @Transactional
    public Item save(ItemPojo itemPojo) {
        Item item = new Item();
        item.setDescription(itemPojo.getDescription());
        item.setNearestLandmark(itemPojo.getNearestLandmark());
        item.setUsers(userService.findById(itemPojo.getUserId()));
        item.setItemStatus(itemPojo.getItemStatus().name());
        item.setDateReported(itemPojo.getReportedDate());
        item.setLocation(addressService.save(itemPojo.getAddressPojo(), null));
        item.setDescriptiveAttributes(descriptiveAttributesService.save(itemPojo.getDescriptiveAttributesPojo()));
        item.setItemCategoryList(itemPojo.getItemCategoryIds().stream().map(itemCategoryService::findById).toList());
        item.setItemPhoto(fileService.findById(itemPojo.getItemPhotoId()));
        return itemRepository.save(item);
    }

    @Override
    public Page<Item> findAllItems(String itemStatus, int page, int size, Long itemCategoryId) {
        Pageable pageable = PageRequest.of(page, size);
        if (Objects.nonNull(itemCategoryId)) {
            return itemRepository.findAllByItemStatusAndItemCategoryListContains(itemStatus, itemCategoryService.findById(itemCategoryId), pageable);
        }
        return itemRepository.findAllByItemStatus(itemStatus, pageable);
    }

    @Transactional
    public void changeItemStatus(Long itemId) {
        Item item = findById(itemId);
        item.setItemStatus(ItemStatus.CLAIMED.name());
        itemRepository.save(item);
    }

    @Override
    public List<Item> findBestMatchItemsByItemId(Long lostItemId) {
        List<Item> bestMatchItems = new ArrayList<>();

        Item lostItem = findById(lostItemId);
        List<ItemCategory> lostItemCategories = lostItem.getItemCategoryList();

        List<Item> foundItems = lostItemCategories.stream()
                .filter(category -> category.getLevel() == 1)
                .flatMap(category -> itemRepository.findAllByItemStatusAndItemCategoryListContains(FOUND.name(), itemCategoryService.findById(category.getId()), Pageable.unpaged()).getContent().stream())
                .toList();

        if (foundItems.isEmpty()) {
            return bestMatchItems;
        }

        // Filter items based on distance
        List<Item> nearbyFoundItems = foundItems.stream().filter(item -> distanceBetween(
                lostItem.getLocation().getLatitude(),
                lostItem.getLocation().getLongitude(),
                item.getLocation().getLatitude(),
                item.getLocation().getLongitude())).toList();

        return analyzeDescriptiveAttributes(nearbyFoundItems, lostItem, bestMatchItems);
    }

    /**
     * Calculates the great-circle distance between two points on the Earth's surface
     * using the Haversine formula.
     *
     * @param latitude1  the latitude of the first point in degrees
     * @param longitude1 the longitude of the first point in degrees
     * @param latitude2  the latitude of the second point in degrees
     * @param longitude2 the longitude of the second point in degrees
     * @return flag for the distance between the two points is within one kilometer
     */
    private boolean distanceBetween(Double latitude1, Double longitude1, Double latitude2, Double longitude2) {
        final int R = 6371;
        double latDistance = Math.toRadians(latitude2 - latitude1);
        double lonDistance = Math.toRadians(longitude2 - longitude1);
        double centralAngle = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(centralAngle), Math.sqrt(1 - centralAngle));
        System.out.println("Distance: " + R * c);
        return R * c <= 1;  //less or equal than 1 km
    }

    /**
     * Analyzes the descriptive attributes of a lost item against a list of found items
     * to identify potential matches based on a fuzzy similarity score.
     * <p>
     * This method processes the attributes of the lost item and each found item,
     * focusing on items in the "Electronics Devices" category with non-null descriptive attributes
     * (brand and color). A match is determined if the fuzzy similarity score meets a specified threshold.
     *
     * @param nearbyFoundItems A list of found items to be analyzed for potential matches.
     * @param lostItem         The lost item whose descriptive attributes are compared.
     * @param bestMatchItems   A list to store items that meet the similarity threshold as potential matches.
     * @return A list of matched items if any meet the threshold, otherwise the original list of found items.
     */

    private List<Item> analyzeDescriptiveAttributes(List<Item> nearbyFoundItems, Item lostItem, List<Item> bestMatchItems) {
        if (nearbyFoundItems.isEmpty()) {
            return bestMatchItems;
        }
        String lostItemAttributes = getProcessedAttributes(lostItem);
        if (!lostItemAttributes.isEmpty()) {
            for (Item foundItem : nearbyFoundItems) {
                String foundItemAttributes;
                for (ItemCategory innerItem : foundItem.getItemCategoryList()) {
                    if (innerItem.getLevel() == 0 && ELECTRONICS_CATEGORY.equals(innerItem.getName())) {
                        if (Objects.nonNull(foundItem.getDescriptiveAttributes()) && Objects.nonNull(foundItem.getDescriptiveAttributes().getBrand()) && Objects.nonNull(foundItem.getDescriptiveAttributes().getColor())) {
                            foundItemAttributes = getProcessedAttributes(foundItem);
                            FuzzyScore fuzzyScore = new FuzzyScore(Locale.ENGLISH);
                            int score = fuzzyScore.fuzzyScore(lostItemAttributes, foundItemAttributes);
                            int maxPossibleScore = Math.max(lostItemAttributes.length(), foundItemAttributes.length());
                            double normalizedScore = (double) score / maxPossibleScore;
                            System.out.println("Normalized Score: " + normalizedScore);
                            if (normalizedScore > 0.8) {
                                bestMatchItems.add(foundItem);
                            }
                        }
                    }
                }
            }
        }
        return bestMatchItems.isEmpty() ? nearbyFoundItems : bestMatchItems;
    }
}
