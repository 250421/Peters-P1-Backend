package com.project1.service;

import com.project1.entity.Account;
import com.project1.entity.Item;
import com.project1.repository.AccountRepository;
import com.project1.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ItemService {

    private static ItemRepository itemRepository;
    private static AccountRepository accountRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository, AccountRepository accountRepository) {
        ItemService.itemRepository = itemRepository;
        ItemService.accountRepository = accountRepository;
    }

    public void addItem(Item item) {
        Item existingItem = itemRepository.findByNameIgnoreCase(item.getName());
        if (existingItem != null) {
            return;
        }
        if(Objects.equals(item.getName(), "")) {
            return;
        }

        itemRepository.save(item);
    }

    public Item findByName(String name) {
        return itemRepository.findByNameIgnoreCase(name);
    }

    public List<Item> getAllItems() {
        return (List<Item>) itemRepository.findAll();
    }

    public Item findById(int id) {
        return itemRepository.findById(id);
    }

    public void deleteById(int id) {
        itemRepository.deleteItemUsingProcedure(id);
    }

    public void updateItem(int itemId, Item item) {
        Item i = findById(itemId);
        i.setName(item.getName());
        i.setType(item.getType());
        i.setDescription(item.getDescription());
        i.setImage_url(item.getImage_url());
        i.setPrice(item.getPrice());
        itemRepository.save(i);
    }

    public void hideItem(Item item) {
        item.setIsVisible(!item.getIsVisible());
        itemRepository.save(item);
    }

    public List<Item> findByNameContaining(String name) {
        return itemRepository.findByIsVisibleTrueAndNameContainingIgnoreCase(name);
    }

    public List<Item> findByType(String type) {
        return itemRepository.findByIsVisibleTrueAndTypeIgnoreCase(type);
    }

    public List<Item> getHiddenItems() {
        return itemRepository.findByIsVisibleFalse();
    }

    public String getUserEmailByUserId(int itemid) {
        return itemRepository.findUserEmailByUserId(itemid);
    }
}
