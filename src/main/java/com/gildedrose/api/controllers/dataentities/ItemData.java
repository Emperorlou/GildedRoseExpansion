package com.gildedrose.api.controllers.dataentities;


/**
 * This class is essentially identical to the ItemEntity, which seems redundant, but in a real
 * production scenario they would often be separate classes. This also allows us to assign meaning
 * to returning one or the other class since the ItemData fields (namely price), unlike ItemEntity,
 * has the true price built in and not just the "base" price.
 */
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
