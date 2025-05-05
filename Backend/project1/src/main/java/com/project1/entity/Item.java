package com.project1.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name="items")
@NamedStoredProcedureQuery(
        name = "delete_item_and_junctions",
        procedureName = "delete_item_and_junctions",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "itemId", type = Integer.class)
        }
)
public class Item {

    @Column(name="id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String type;
    private String description;
    private String image_url;
    @Column(name = "isvisible")
    private Boolean isVisible = true;
    @Column(name = "user_id", nullable = false)
    private int userId;
    //new
    private double price;


    public Item () {

    }

    public Item (Integer id, String name,String type, String description, String image_url, Boolean isVisible,
                 Integer user_id, Double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image_url= image_url;
        this.isVisible = isVisible;
        this.userId = user_id;
        this.price = price;
    }

    public Item (String name, String type, String description, String image_url, Boolean isVisible, Integer userId,
                 Double price) {
        this.name = name;
        this.description = description;
        this.image_url= image_url;
        this.isVisible = isVisible;
        this.userId = userId;
        this.price = price;
    }


    public void setId(Integer id) {
        this.id = id;
    }

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setUser(Integer userId) {
        this.userId = userId;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }
    public Boolean getIsVisible() {
        return isVisible;
    }
    public String getImage_url() {
        return image_url;
    }

    public Integer getUser() {
        return userId;
    }

    public double getPrice() {
        return price;
    }
}
