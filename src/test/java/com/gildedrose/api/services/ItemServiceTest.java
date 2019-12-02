package com.gildedrose.api.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ItemServiceTest {


    /**
     * This test has an exceedingly low (though possible) chance of failure if it happens to start
     * within 1 ms of the change of the hour.
     */
    @Test
    public void viewingActivityMonitorTest()
    {
        ItemService itemService = new ItemServiceImpl(new MemcacheServiceImpl());

        long currentTimeMs = System.currentTimeMillis();

        ((ItemServiceImpl)itemService).flagItemsViewed();
        assertEquals(1, itemService.getSurgeRangeViewCount(currentTimeMs, 0));
        assertEquals(false, itemService.isSurgeDetected(currentTimeMs));

        for(int i = 0; i<itemService.getSurgeTrigger(); i++)
            ((ItemServiceImpl)itemService).flagItemsViewed();

        assertEquals(itemService.getSurgeTrigger()+1, itemService.getSurgeRangeViewCount(currentTimeMs, 0));
        assertEquals(true, itemService.isSurgeDetected(currentTimeMs));
    }

    /**
     * This test is used to check that a surge can be triggered, but also that it stops being triggered
     * after an expected amount of time has passed.
     */
    @Test
    public void surgeCompleteTest()
    {
        ItemService itemService = new ItemServiceImpl(new MemcacheServiceImpl());
        itemService.setSurgePeriod(2);  // Setting the surge period to 2 seconds (default is 1 hour) to simplify things for the test

        long currentTimeMs = System.currentTimeMillis();

        assertEquals(false, itemService.isSurgeDetected(currentTimeMs));

        for(int i = 0; i<itemService.getSurgeTrigger()+1; i++)
            ((ItemServiceImpl)itemService).flagItemsViewed();

        assertEquals(itemService.getSurgeTrigger()+1, itemService.getSurgeRangeViewCount(currentTimeMs, 0));
        assertEquals(true, itemService.isSurgeDetected(currentTimeMs));

        // Here time is passing but not enough to untrigger the surge detection
        currentTimeMs += 1000;

        assertEquals(true, itemService.isSurgeDetected(currentTimeMs));

        // Here time would pass enough to untrigger the surge
        currentTimeMs += 2000;

        assertEquals(false, itemService.isSurgeDetected(currentTimeMs));
    }




}
