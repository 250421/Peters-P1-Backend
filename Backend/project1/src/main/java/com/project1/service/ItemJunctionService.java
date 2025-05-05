package com.project1.service;

import com.project1.entity.Account;
import com.project1.entity.Item;
import com.project1.entity.ItemsJunction;
import com.project1.repository.ItemsJunctionRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ItemJunctionService {

    private ItemsJunctionRepository itemsJunctionRepository;

    public ItemJunctionService(ItemsJunctionRepository itemsJunctionRepository) {
        this.itemsJunctionRepository = itemsJunctionRepository;
    }

    public List<Item> findItemsByUserAndIsVisibleTrue(Account user) {
        return itemsJunctionRepository.findItemsByUserAndIsVisibleTrue(user);
    }

    public boolean existsByItemAndUser(Item item, Account user) {
        return itemsJunctionRepository.existsByItemAndUser(item,user);
    }

    public void saveItem(ItemsJunction itemsJunction) {
        itemsJunctionRepository.save(itemsJunction);
    }

    public void removeLikedItem(int itemId, int userId) {
        itemsJunctionRepository.deleteByItemIdAndUserId(itemId, userId);
    }

    public int getNumSavedItems(int userId) {
        return itemsJunctionRepository.countByUserId(userId);
    }
}
