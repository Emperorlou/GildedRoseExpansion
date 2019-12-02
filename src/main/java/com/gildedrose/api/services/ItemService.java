package com.gildedrose.api.services;

import com.gildedrose.api.controllers.dataentities.ItemData;

import java.util.Collection;

public interface ItemService {
    /**
     * This method will return all the items in the database, but is specifically meant to be used
     * for client-facing item lists.
     *
     * @return All items available for purchase
     */
    Collection<ItemData> fetchAllItems_ForUserView();

    int getSurgePeriod();

    void setSurgePeriod(int seconds);

    int getSurgeTrigger();

    boolean isSurgeDetected(long currentTimeMs);

    int getSurgeRangeViewCount(long currentTimeMs, int relativeRangeIndex);

    ItemData getItemByName(String itemName);

    double getSurgePriceMultiplier();
}
