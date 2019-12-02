package com.gildedrose.api.services;

import com.gildedrose.api.controllers.dataentities.ItemData;
import com.gildedrose.api.dbentities.ItemEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * This injected service holds the business logic as it pertains to items in the store.
 *
 * Notes:
 *  - Since we're not using a database for this test, we're instead using a key/value store for the items.
 *  -
 */
@Service
public class ItemServiceImpl implements ItemService {
    public static final String MC_PREFIX_SURGE_RANGE_INDEX = "surgerangeindex-";
    private static Map<String, ItemEntity> items = new HashMap<>();

    int surgePeriod = 3600;  // Default surge period is 1 hour

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

    /**
     * This constructor is used in tests.
     * @param memcache
     */
    ItemServiceImpl(MemcacheService memcache)
    {
        this.memcache = memcache;
    }

    /**
     * This method will not only fetch the items from the database but will also trigger
     * a "view" for surge detection. The items this method returns include any price
     * adjustments as well.
     * @return
     */
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

    /**
     * This is half of the time period we want to monitor for a surge and it is used
     * when monitoring view counts. View counts are tracked "per range". For example,
     * if the period we want to monitor for surges is 1 hour then the range is 30 minutes
     * and the view counts we care about are the 3 most recent ranges. For more details on why
     * it is done this way, see this class's javadoc.
     * @return
     */
    public int getSurgeRange() {
        return surgePeriod/2;
    }

    /**
     * The time period that we are looking for a surge in viewing traffic.
     * @return Seconds
     */
    @Override
    public int getSurgePeriod() {
        return surgePeriod;
    }

    /**
     * Adjusting the surge period is used in tests.
     *
     * The number of seconds used cannot be lower than 2 or an IllegalArgumentException will be thrown.
     *
     * @param seconds
     */
    @Override
    public void setSurgePeriod(int seconds) {
        if (seconds<2) throw new IllegalArgumentException("Surge period cannot be set lower than 2.");
        this.surgePeriod = seconds;
    }

    /**
     * The number of views in a given surge-range that will trigger the surge detection.
     * @return
     */
    @Override
    public int getSurgeTrigger() {
        return 10;
    }

    /**
     * The multiplier that is applied to the price if a surge is detected.
     * @return
     */
    @Override
    public double getSurgePriceMultiplier() {
        return 1.1d;
    }

    /**
     * @return True if a surge is currently detected as in progress.
     */
    @Override
    public boolean isSurgeDetected(long currentTimeMs) {
        int surgeTrigger = getSurgeTrigger();
        boolean period1Surge = (getSurgeRangeViewCount(currentTimeMs, 0) + getSurgeRangeViewCount(currentTimeMs, -1)>surgeTrigger);
        boolean period2Surge = (getSurgeRangeViewCount(currentTimeMs, -1) + getSurgeRangeViewCount(currentTimeMs, -2)>surgeTrigger);

        return (period1Surge || period2Surge);
    }

    /**
     * The number of views that the items list has seen in the "surge range" (time period). For example,
     * if our surge range is 1 hour and it is 4:15pm then this method will return the number of views that occurred
     * between 4pm and 5pm.
     * @return Number of views
     */
    @Override
    public int getSurgeRangeViewCount(long currentTimeMs, int relativeRangeIndex) {
        Integer count = (Integer)memcache.get(MC_PREFIX_SURGE_RANGE_INDEX + (getCurrentRangeIndex(currentTimeMs)+relativeRangeIndex));
        if (count==null) return 0;
        return count;
    }


    /**
     * Fetches an item from our "database" by ID (name in this case).
     * The return is not just the raw data from the database but includes any adjustments
     * to make the data ready for viewing by the user.
     *
     * @param itemName
     * @return The user-facing item data
     */
    @Override
    public ItemData getItemByName(String itemName) {
        return createUserViewItemFromDBItem(items.get(itemName));
    }

    /**
     * This generates an ItemData object from an ItemEntity object. The resulting item data
     * will have what we want the user to see.
     * @param dbItem
     * @return
     */
    ItemData createUserViewItemFromDBItem(ItemEntity dbItem) {
        if (dbItem==null) return null;

        int price = dbItem.getPrice();

        if (isSurgeDetected(System.currentTimeMillis()))
            price = (int)Math.round(((double)price)*getSurgePriceMultiplier());

        return new ItemData(dbItem.getName(),dbItem.getDescription(), price);
    }

    /**
     * This returns the current range index we're in. For example,
     * if our surge range is 1 hour and it is 4:49pm then this method will return the number of hours
     * that have passed until 4pm since epoch.
     * @return The current range index (aka the current hour) since epoch
     */
    long getCurrentRangeIndex(long currentTimeMs) {
        return currentTimeMs/(getSurgeRange()*1000);
    }

    /**
     * This will store in memcache when we recieve a view on the items list, the view will
     * be logged into the current surge range index.
     * (See the javadocs on the other methods in this class to get a complete understanding
     * of what that means)
     */
    void flagItemsViewed() {
        long currentTimeMs = System.currentTimeMillis();
        int count = getSurgeRangeViewCount(currentTimeMs, 0);
        count++;
        memcache.put(MC_PREFIX_SURGE_RANGE_INDEX + getCurrentRangeIndex(currentTimeMs), count);
    }

}
