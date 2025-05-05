package com.project1;

import com.project1.entity.Account;
import com.project1.entity.Item;
import com.project1.entity.ItemsJunction;
import com.project1.repository.AccountRepository;
import com.project1.repository.ItemsJunctionRepository;
import com.project1.service.AccountService;
import com.project1.service.ItemJunctionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class ItemJunctionServiceTest {

    @Mock
    private ItemsJunctionRepository itemsJunctionRepository;

    @InjectMocks
    private ItemJunctionService itemJunctionService;


    @Test
    void findItemsByUserAndIsVisibleTrue_returnsVisibleItems() {
        Account user = new Account();
        List<Item> items = List.of(new Item(), new Item());

        when(itemsJunctionRepository.findItemsByUserAndIsVisibleTrue(user)).thenReturn(items);

        List<Item> result = itemJunctionService.findItemsByUserAndIsVisibleTrue(user);

        assertEquals(2, result.size());
        verify(itemsJunctionRepository).findItemsByUserAndIsVisibleTrue(user);
    }

    @Test
    void existsByItemAndUser_exists() {
        Item item = new Item();
        Account user = new Account();

        when(itemsJunctionRepository.existsByItemAndUser(item, user)).thenReturn(true);

        boolean result = itemJunctionService.existsByItemAndUser(item, user);

        assertTrue(result);
        verify(itemsJunctionRepository).existsByItemAndUser(item, user);
    }

    @Test
    void saveItem_saves() {
        Item item = new Item();
        Account user = new Account();
        ItemsJunction ij = new ItemsJunction(item, user);

        itemJunctionService.saveItem(ij);

        verify(itemsJunctionRepository).save(ij);
    }

    @Test
    void removeLikedItem_removesItem() {
        Item item = new Item();
        Account user = new Account();
        item.setId(3);
        user.setAccountId(67);

        itemJunctionService.removeLikedItem(3,67);

        verify(itemsJunctionRepository).deleteByItemIdAndUserId(item.getId(), user.getId());
    }

   /* @Test
    void getNumSavedItems_returnsCorrectNum() {
        int userId = 12;
        when(itemsJunctionRepository.countByUserId(userId)).thenReturn(5);

        int result = itemJunctionService.getNumSavedItems(userId);

        assertEquals(5, result);
        verify(itemsJunctionRepository).countByUserId(userId);
    }

    @Test
    void getNumSavedItems_returnsIncorrectNum() {
        int userId = 12;
        when(itemsJunctionRepository.countByUserId(userId)).thenReturn(4);

        int result = itemJunctionService.getNumSavedItems(userId);

        assertNotEquals(5, result);
        verify(itemsJunctionRepository).countByUserId(userId);
    }*/
}
