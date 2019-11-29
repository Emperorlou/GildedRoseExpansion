package com.gildedrose.api.controllers.dataentities;

public class ItemData {
    private String name;
    private String description;
    private Integer price;

    public ItemData(String name, String description, int price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(int price)
    {
        this.price = price;
    }

}
