package com.project1.controller;

import com.project1.ErrorResponse;
import com.project1.entity.*;
import com.project1.service.AccountService;
import com.project1.service.ItemJunctionService;
import com.project1.service.ItemService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")

public class AppController {
    private AccountService accountService;
    private ItemService itemService;
    private ItemJunctionService itemJunctionService;

    @Autowired
    public AppController(AccountService accountService, ItemService itemService,
                         ItemJunctionService itemJunctionService) {
        this.accountService = accountService;
        this.itemService = itemService;
        this.itemJunctionService = itemJunctionService;
    }

    @PostMapping("/auth/sign-up")
    public ResponseEntity<?> register(@RequestBody Account account, HttpServletRequest request) {
        // Check if the user is already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            ErrorResponse e = new ErrorResponse("You are already logged in.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e);
        }

        // Check if the account already exists
        if (accountService.findByUsername(account.getEmail()) != null) {
            ErrorResponse e = new ErrorResponse("Email already exists");
            return ResponseEntity.status(409).body(e);
        }

        // Validate account fields
        if (account.getEmail().length() < 1) {
            ErrorResponse e = new ErrorResponse("Email can't be empty");
            return ResponseEntity.status(400).body(e);
        }
        if (!account.getEmail().matches(".*[@].*")) {
            ErrorResponse e = new ErrorResponse("Invalid Email");
            return ResponseEntity.status(400).body(e);
        }
        if (account.getPassword().length() < 8) {
            ErrorResponse e = new ErrorResponse("Password must be at least 8 characters long");
            return ResponseEntity.status(400).body(e);
        }
        if (!account.getPassword().matches(".*[a-z].*")) {
            ErrorResponse e = new ErrorResponse("Password must contain at least one lowercase letter\"");
            return ResponseEntity.status(400).body(e);
        }
        if (!account.getPassword().matches(".*[A-Z].*")) {
            ErrorResponse e = new ErrorResponse("Password must contain at least one uppercase letter");
            return ResponseEntity.status(400).body(e);
        }
        if (!account.getPassword().matches(".*\\d.*")) {
            ErrorResponse e = new ErrorResponse("Password must contain at least one number");
            return ResponseEntity.status(400).body(e);
        }
        if (!account.getPassword().matches(".*[@$!%*?&].*")) {
            ErrorResponse e = new ErrorResponse("Password must contain at least one special character (@$!%*?&)");
            return ResponseEntity.status(400).body(e);
        }

        // Register the new user
        accountService.registerNewUser(account);
        AccountDTO accountDTO = new AccountDTO(account.getId(), account.getEmail(), account.getRole());
        return ResponseEntity.status(201)
                .body(accountDTO);
    }

    @PostMapping("/auth/sign-in")
    public ResponseEntity<?> login(@RequestBody Account account, HttpServletRequest request) {
        // Check if the user is already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            ErrorResponse e = new ErrorResponse("You are already logged in.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e);
        }

        // Attempt login
        Account found = accountService.login(account.getEmail(), account.getPassword());
        if (found == null) {
            ErrorResponse e = new ErrorResponse("Invalid credentials.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e);
        }

        // Create a session and store user information
        session = request.getSession(true);
        AccountDTO dto = new AccountDTO(found.getId(), found.getEmail(), found.getRole());
        session.setAttribute("user", dto);

        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @GetMapping("/auth")
    public ResponseEntity<?> profile(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            System.out.println("Session is null");
            ErrorResponse e = new ErrorResponse("Access denied: User not logged in.");
            return ResponseEntity.status(401).body(e);
        }

        Object user = session.getAttribute("user");
        if (user == null) {
            ErrorResponse e = new ErrorResponse("Access denied: User not logged in.");
            return ResponseEntity.status(401).body(e);
        }

        AccountDTO accountDTO = (AccountDTO) user;
        return ResponseEntity.ok(accountDTO);
    }

    @GetMapping("/items")
    public ResponseEntity<List<Item>> getItems() {
        return ResponseEntity.status(200).body(itemService.getAllItems());
    }

    @PostMapping("/items")
    public ResponseEntity<?> addItem(@RequestBody Item item) {
        if(itemService.findByName(item.getName()) == null) {
            itemService.addItem(item);
            return ResponseEntity.status(201).body(item);
        } else {
            ErrorResponse e = new ErrorResponse("Item name already exists");
            return ResponseEntity.status(409).body(e);
        }
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<?> getItemsById(@PathVariable int id) {
        if(itemService.findById(id) == null) {
            ErrorResponse e = new ErrorResponse("Item id not found");
            return ResponseEntity.status(404).body(e);
        }
        return new ResponseEntity<>(itemService.findById(id), HttpStatus.OK);
    }

    @DeleteMapping("items/{id}")
    public ResponseEntity<?> deleteItemById(@PathVariable int id) {
        if(itemService.findById(id) == null) {
            return ResponseEntity.status(404).build();
        }
        itemService.deleteById(id);
        ErrorResponse e = new ErrorResponse("Item successfully deleted.");
        return ResponseEntity.status(HttpStatus.OK).body(e);
    }

    @PatchMapping("items/{id}")
    public ResponseEntity<?> updateItem(@PathVariable int id, @RequestBody Item item) {
        Item i = itemService.findById(id);
        if(i == null) {
            ErrorResponse e = new ErrorResponse("Item ID doesnt exist");
            return ResponseEntity.status(404).body(e);
        }
        if(i.getName().equalsIgnoreCase(item.getName())) {
            itemService.updateItem(id, item);
            return ResponseEntity.status(200).body(item);
        }
        if (itemService.findByName(item.getName()) != null) {
            ErrorResponse e = new ErrorResponse("Item with this name already exists");
            return ResponseEntity.status(409).body(e);
        } else {
            itemService.updateItem(id, item);
            return ResponseEntity.status(200).body(item);
        }
    }

    @GetMapping("/items/search")
    public ResponseEntity<?> searchForItem(@RequestParam String name) {
        List<Item> items = itemService.findByNameContaining(name);
        if(items.size() > 0) {
           return ResponseEntity.status(200).body(items);
        }
        ErrorResponse e = new ErrorResponse("No items found");
        return ResponseEntity.status(200).body(e);
    }

    @GetMapping("/items/type")
    public ResponseEntity<?> searchByType(@RequestParam String type) {
        List<Item> items = itemService.findByType(type);
        if(items.size() > 0) {
            return ResponseEntity.status(200).body(items);
        }
        ErrorResponse e = new ErrorResponse("No items of "+ type + " type found");
        return ResponseEntity.status(200).body(e);
    }

    @PatchMapping("/items/{id}/visibility")
    public ResponseEntity<?> hideItem(@PathVariable int id) {
        Item i = itemService.findById(id);
        if(i == null) {
            ErrorResponse e = new ErrorResponse("Item ID doesn't exist");
            return ResponseEntity.status(404).body(e);
        }
        itemService.hideItem(i);
        return ResponseEntity.ok(new ErrorResponse("Item is now hidden"));
    }

    @GetMapping("/items/likedItems/{id}")
    public ResponseEntity<?> viewLikedItems(@PathVariable int id) {
        Account user = accountService.findByAccountId(id);
        if(user == null) {
            ErrorResponse e = new ErrorResponse("User with id " + id + " doesnt exist");
            return ResponseEntity.status(404).body(e);
        }
        List<Item> likedItems = itemJunctionService.findItemsByUserAndIsVisibleTrue(user);
        if (likedItems.size() == 0) {
            ErrorResponse e = new ErrorResponse("User currently has no saved items");
            return ResponseEntity.status(200).body(e);
        }
        return ResponseEntity.status(200).body(likedItems);
    }

    @PostMapping("/items/addLikedItems")
    public ResponseEntity<?> saveItem(@RequestBody SavedItemDTO savedItemDTO) {
        Item item = itemService.findById(savedItemDTO.getItemId());
        Account user = accountService.findByAccountId(savedItemDTO.getUserId());
        if(item == null || user == null) {
            return ResponseEntity.status(401).build();
        }
        boolean alreadySaved = itemJunctionService.existsByItemAndUser(item, user);
        if(alreadySaved) {
            ErrorResponse e = new ErrorResponse("User already has item saved");
            return ResponseEntity.status(409).body(e);
        }
        ItemsJunction j = new ItemsJunction();
        j.setItem(item);
        j.setUser(user);
        itemJunctionService.saveItem(j);

        ErrorResponse e = new ErrorResponse("Successfully saved item");
        return ResponseEntity.status(200).body(e);
    }

    @DeleteMapping("/items/removeSavedItem")
    public ResponseEntity<?> unSaveItem(@RequestParam int itemId, @RequestParam int userId ) {
        Item item = itemService.findById(itemId);
        Account user = accountService.findByAccountId(userId);
        if(item == null || user == null) {
            ErrorResponse e = new ErrorResponse("Item or user not found");
            return ResponseEntity.status(404).body(e);
        }

        boolean exists = itemJunctionService.existsByItemAndUser(item, user);
        if(!exists) {
            ErrorResponse e = new ErrorResponse("Item is not saved");
            return ResponseEntity.status(404).body(e);
        }

        itemJunctionService.removeLikedItem(itemId, userId);
        ErrorResponse e = new ErrorResponse("Item successfully removed");
        return ResponseEntity.status(200).body(e);
    }

    @GetMapping("/items/isSaved")
    public ResponseEntity<Boolean> isItemSaved(@RequestParam int itemId, @RequestParam int userId) {
        Item i = itemService.findById(itemId);
        Account a = accountService.findByAccountId(userId);
        boolean isSaved = (itemJunctionService.existsByItemAndUser(i,a));
        return ResponseEntity.ok(isSaved);
    }

    @GetMapping("/items/getHiddenItems")
    public ResponseEntity<?> getHiddenItems() {
        List<Item> hiddenItems = itemService.getHiddenItems();
        if(hiddenItems.size() == 0) {
            ErrorResponse e = new ErrorResponse("No items are currently out of stock");
            return ResponseEntity.status(200).body(e);
        }

        return ResponseEntity.status(200).body(hiddenItems);
    }

    @GetMapping("items/numberOfSavedItems")
    public ResponseEntity<?> getNumOfSavedItems(@RequestParam int userid) {
        Account a = accountService.findByAccountId(userid);
        if(a == null) {
            ErrorResponse e = new ErrorResponse("No user with id " + userid);
            return ResponseEntity.status(200).body(e);
        }
        int numItems = itemJunctionService.getNumSavedItems(userid);
        return ResponseEntity.status(200).body(numItems);
    }

    @GetMapping("/items/getUserPostedBy")
    public ResponseEntity<?> getPostedEmail(@RequestParam int itemid) {
        Item a = itemService.findById(itemid);
        if (a == null) {
            ErrorResponse e = new ErrorResponse("No item with id " + itemid);
            return ResponseEntity.status(404).body(e);
        }
        String email = itemService.getUserEmailByUserId(itemid);
        return ResponseEntity.status(200).body(email);
    }


}