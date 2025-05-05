package com.project1.repository;

import com.project1.entity.Account;
import com.project1.entity.Item;
import com.project1.entity.ItemsJunction;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemsJunctionRepository extends JpaRepository<ItemsJunction, Integer> {

    @Query("SELECT ij.item FROM ItemsJunction ij WHERE ij.user.id = :userId")
    List<Item> findItemsByUserId(@Param("userId") int userId);
    boolean existsByItemAndUser(Item item, Account user);

    @Query("SELECT ij.item FROM ItemsJunction ij WHERE ij.user = :user AND ij.item.isVisible = true")
    List<Item> findItemsByUserAndIsVisibleTrue(Account user);

    @Modifying
    @Transactional
    @Query("DELETE FROM ItemsJunction ij WHERE ij.item.id = :itemId AND ij.user.id = :userId")
    void deleteByItemIdAndUserId(int itemId, int userId);

    @Query(value = "select COUNT(*) from itemsjunction join items on itemsjunction.item_id = items.id where " +
            "itemsjunction.user_id = :userId AND items.isVisible = true", nativeQuery = true)
    int countByUserId(@Param("userId") int userId);


}
