package com.project1;

import com.project1.entity.Account;
import com.project1.entity.Item;
import com.project1.repository.ItemRepository;
import com.project1.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    private Item item;
    private Account user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        item = new Item(1, "Item1", "Fire", "Desc", "image_url", true, 76, 5.99);
    }

    @Test
    void addItem_NewItem_Saves() {
        when(itemRepository.findByNameIgnoreCase("Item1")).thenReturn(null);
        itemService.addItem(item);
        verify(itemRepository).save(item);
    }

    @Test
    void addItem_ExistingItem_DoesNotSave() {
        when(itemRepository.findByNameIgnoreCase("Item1")).thenReturn(item);
        itemService.addItem(item);
        verify(itemRepository, never()).save(any());
    }

    @Test
    void addItem_EmptyName_DoesNotSave() {
        item.setName("");
        itemService.addItem(item);
        verify(itemRepository, never()).save(any());
    }

    @Test
    void findByName_ReturnsItem() {
        when(itemRepository.findByNameIgnoreCase("Item1")).thenReturn(item);
        Item result = itemService.findByName("Item1");
        assertEquals("Item1", result.getName());
    }

    @Test
    void findByNonExistentName_DoesNotReturnItem() {
        item.setName("fff");
        when(itemRepository.findByNameIgnoreCase("Item1")).thenReturn(item);
        Item result = itemService.findByName("Item1");
        assertNotEquals("Item1", result.getName());
    }

    @Test
    void getAllItems_ReturnsItems() {
        List<Item> items = List.of(item);
        when(itemRepository.findAll()).thenReturn(items);
        List<Item> result = itemService.getAllItems();
        assertEquals(1, result.size());
    }

    @Test
    void findById_ReturnsItem() {
        when(itemRepository.findById(1)).thenReturn(item);
        Item result = itemService.findById(1);
        assertEquals(1, result.getId());
    }

    @Test
    void findByNonExistentId_DoesNotReturnsItem() {
        item.setId(2);
        when(itemRepository.findById(1)).thenReturn(item);
        Item result = itemService.findById(1);
        assertNotEquals(1, result.getId());
    }

    @Test
    void deleteById_DeletesItem() {
        //doNothing().when(itemRepository).deleteById(1);
        itemService.deleteById(1);
        verify(itemRepository).deleteItemUsingProcedure(1);
    }

    @Test
    void updateItem_UpdatesAndSaves() {
        when(itemRepository.findById(1)).thenReturn(item);

        Item updated = new Item();
        updated.setName("NewItem");
        updated.setType("Dark");
        updated.setDescription("NewDesc");
        updated.setImage_url("newImage");

        itemService.updateItem(1, updated);

        assertEquals("NewItem", item.getName());
        assertEquals("Dark", item.getType());
        assertEquals("NewDesc", item.getDescription());
        assertEquals("newImage", item.getImage_url());

        verify(itemRepository).save(item);
    }

    @Test
    void updateItem_NewNameAlreadyExists() {
        Item existingItem = new Item();
        existingItem.setId(2);
        existingItem.setName("NewItem");

        Item currentItem = new Item();
        currentItem.setId(1);
        currentItem.setName("OldItem");
        currentItem.setType("Fire");
        currentItem.setDescription("OldDesc");
        currentItem.setImage_url("oldImage");

        when(itemRepository.findById(1)).thenReturn((currentItem));
        when(itemRepository.findByNameIgnoreCase("NewItem")).thenReturn(existingItem);

        Item updated = new Item();
        updated.setName("NewItem");
        updated.setType("Dark");
        updated.setDescription("NewDesc");
        updated.setImage_url("newImage");

        itemService.findByName(updated.getName());

        // Should NOT update anything due to duplicate name
        assertEquals("OldItem", currentItem.getName());
        assertEquals("Fire", currentItem.getType());
        assertEquals("OldDesc", currentItem.getDescription());
        assertEquals("oldImage", currentItem.getImage_url());

        verify(itemRepository, never()).save(any());
    }

    @Test
    void hideItem_TogglesVisibilityAndSaves() {
        item.setIsVisible(true);
        itemService.hideItem(item);
        assertFalse(item.getIsVisible());
        verify(itemRepository).save(item);

        itemService.hideItem(item);
        assertTrue(item.getIsVisible());
        verify(itemRepository, times(2)).save(item);
    }

    @Test
    void findByNameContaining_ReturnsItems() {
        when(itemRepository.findByIsVisibleTrueAndNameContainingIgnoreCase("Item"))
                .thenReturn(List.of(item));
        List<Item> results = itemService.findByNameContaining("Item");
        assertEquals(1, results.size());
    }

    @Test
    void findByNameContaining_NoContainingName() {
        when(itemRepository.findByIsVisibleTrueAndNameContainingIgnoreCase("Item"))
                .thenReturn(List.of(item));
        List<Item> results = itemService.findByNameContaining("Meti");
        assertEquals(0, results.size());
    }

    @Test
    void findByType_ReturnsItems() {
        when(itemRepository.findByIsVisibleTrueAndTypeIgnoreCase("Fire"))
                .thenReturn(List.of(item));
        List<Item> results = itemService.findByType("Fire");
        assertEquals(1, results.size());
    }

    @Test
    void findByType_CurrentlyNoItemsWithType() {
        when(itemRepository.findByIsVisibleTrueAndTypeIgnoreCase("Fire"))
                .thenReturn(List.of(item));
        List<Item> results = itemService.findByType("Psychic");
        assertEquals(0, results.size());
    }

    @Test
    void getHiddenItems_ReturnsItems() {
        item.setIsVisible(false);
        when(itemRepository.findByIsVisibleFalse()).thenReturn(List.of(item));
        List<Item> results = itemService.getHiddenItems();
        assertEquals(1, results.size());
    }

    @Test
    void getHiddenItems_NoHiddenItems() {
        when(itemRepository.findByIsVisibleFalse()).thenReturn(List.of());
        List<Item> results = itemService.getHiddenItems();
        assertEquals(0, results.size());
    }
}
