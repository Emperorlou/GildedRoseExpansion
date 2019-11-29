package com.gildedrose.api.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ItemServiceTest {

    ItemService itemService = new ItemServiceImpl(new MemcacheServiceImpl());

    /**
     * This test has an exceedingly low (though possible) chance of failure if it happens to start
     * within 1 ms of the change of the hour.
     */
    @Test
    public void viewingActivityMonitorTest()
    {
        ((ItemServiceImpl)itemService).flagItemsViewed();
        assertEquals(1, itemService.getCurrentHourViewCount());
        assertEquals(false, itemService.isSurgeDetected());

        for(int i = 0; i<itemService.getSurgeTrigger(); i++)
            ((ItemServiceImpl)itemService).flagItemsViewed();

        assertEquals(11, itemService.getCurrentHourViewCount());
        assertEquals(true, itemService.isSurgeDetected());

    }

}
