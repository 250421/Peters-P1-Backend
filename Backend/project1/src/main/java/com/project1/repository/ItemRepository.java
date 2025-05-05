package com.project1.repository;

import com.project1.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    Item findByNameIgnoreCase(String name);
    Item findByDescription(String description);
    List<Item> findByIsVisibleTrueAndTypeIgnoreCase(String type);

    List<Item> findByIsVisibleTrueAndNameContainingIgnoreCase(String name);
    Item findById(int id);

    @Procedure(name = "delete_item_and_junctions")
    void deleteItemUsingProcedure(@Param("itemId") int id);

    List<Item> findByIsVisibleTrue();
    List<Item> findByIsVisibleFalse();

    @Query(value = "select email from items join users on items.user_id = users.id where items.id = :itemid",
            nativeQuery = true)
    String findUserEmailByUserId(Integer itemid);
}
