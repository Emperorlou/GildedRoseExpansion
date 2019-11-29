package com.gildedrose.api.services;

import com.gildedrose.api.controllers.dataentities.ItemData;
import com.gildedrose.api.dbentities.ItemEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ItemServiceImpl implements ItemService {
    public static final String MC_PREFIX_HOURLYVIEWS = "hourlyviews-";
    private static Map<String, ItemEntity> items = new HashMap<>();

    @Autowired
    MemcacheService memcache;

    static {
        /*
        Populating the "database" with item entries.
         */
        items.put("Lamp", new ItemEntity("Lamp",
                "An excellent, green, decorative lamp.",
                6000));
        items.put("Door handle", new ItemEntity("Door handle",
                "As always, the most excellent door handle money can buy.",
                3500));
        items.put("Frog leg", new ItemEntity("Frog leg",
                "Delicious in soup, also a great substitute for chicken wings!",
                200));
    }

    public ItemServiceImpl() {
    }

    public ItemServiceImpl(MemcacheService memcache)
    {
        this.memcache = memcache;
    }

    @Override
    public Collection<ItemData> fetchAllItems_ForUserView() {
        // Flag that the items have been viewed for surge tracking
        flagItemsViewed();

        Collection<ItemData> result = new ArrayList<>();

        // Fetch the items from the DB
        Collection<ItemEntity> dbItems = items.values();


        // Populate the resulting items list with surge pricing if necessary
        for(ItemEntity dbItem:dbItems)
            result.add(createUserViewItemFromDBItem(dbItem));

        return result;
    }

    @Override
    public int getSurgeTrigger() {
        return 10;
    }

    @Override
    public double getSurgePriceMultiplier() {
        return 1.1d;
    }

    @Override
    public boolean isSurgeDetected() {
        int surgeTrigger = getSurgeTrigger();
        return (getCurrentHourViewCount()>surgeTrigger || getPreviousHourViewCount()>surgeTrigger);
    }

    @Override
    public int getCurrentHourViewCount() {
        Integer count = (Integer)memcache.get(MC_PREFIX_HOURLYVIEWS + getCurrentHour());
        if (count==null) return 0;
        return count;
    }

    @Override
    public int getPreviousHourViewCount() {
        Integer count = (Integer)memcache.get(MC_PREFIX_HOURLYVIEWS + (getCurrentHour()-1));
        if (count==null) return 0;
        return count;
    }

    @Override
    public ItemData getItemByName(String itemName) {
        return createUserViewItemFromDBItem(items.get(itemName));
    }

    ItemData createUserViewItemFromDBItem(ItemEntity dbItem) {
        if (dbItem==null) return null;

        int price = dbItem.getPrice();

        if (isSurgeDetected())
            price = (int)Math.round(((double)price)*getSurgePriceMultiplier());

        return new ItemData(dbItem.getName(),dbItem.getDescription(), price);
    }

    long getCurrentHour() {
        return System.currentTimeMillis()/3600000;
    }

    void flagItemsViewed() {
        int count = getCurrentHourViewCount();
        count++;
        memcache.put(MC_PREFIX_HOURLYVIEWS + getCurrentHour(), count);
    }

}
