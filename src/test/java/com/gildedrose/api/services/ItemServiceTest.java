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

        ((ItemServiceImpl)itemService).flagItemsViewed();
        assertEquals(1, itemService.getCurrentSurgeRangeViewCount());
        assertEquals(false, itemService.isSurgeDetected());

        for(int i = 0; i<itemService.getSurgeTrigger(); i++)
            ((ItemServiceImpl)itemService).flagItemsViewed();

        assertEquals(itemService.getSurgeTrigger()+1, itemService.getCurrentSurgeRangeViewCount());
        assertEquals(true, itemService.isSurgeDetected());
    }

    /**
     * This test is used to check that a surge can be triggered, but also that it stops being triggered
     * after an expected amount of time has passed.
     */
    @Test
    public void surgeCompleteTest()
    {
        ItemService itemService = new ItemServiceImpl(new MemcacheServiceImpl());
        itemService.setSurgeRange(1);

        assertEquals(false, itemService.isSurgeDetected());

        for(int i = 0; i<itemService.getSurgeTrigger()+1; i++)
            ((ItemServiceImpl)itemService).flagItemsViewed();

        assertEquals(itemService.getSurgeTrigger()+1, itemService.getCurrentSurgeRangeViewCount());
        assertEquals(true, itemService.isSurgeDetected());

        try {
            Thread.sleep(1001);
        } catch (InterruptedException e) {
            throw new RuntimeException("Cancelled");
        }

        assertEquals(true, itemService.isSurgeDetected());

        try {
            Thread.sleep(1001);
        } catch (InterruptedException e) {
            throw new RuntimeException("Cancelled");
        }

        assertEquals(false, itemService.isSurgeDetected());
    }




}
