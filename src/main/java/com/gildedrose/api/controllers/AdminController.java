package com.gildedrose.api.controllers;

import com.gildedrose.api.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AdminController {

    @Autowired
    ItemService itemService;

    /**
     * This is a handy admin tool for seeing what is going on with some of the important surge-related variables.
     * @return
     */
    @RequestMapping(value = "/admin/config", produces = "application/json")
    public Map config() {
        Map<String, Object> result = new HashMap<>();

        long currentTimeMs = System.currentTimeMillis();

        result.put("surgeRange", itemService.getSurgePeriod());
        result.put("surgeTrigger", itemService.getSurgeTrigger());
        result.put("surgePriceMultiplier", itemService.getSurgePriceMultiplier());
        result.put("rangeViewCountForA", itemService.getSurgeRangeViewCount(currentTimeMs, 0));
        result.put("rangeViewCountForB", itemService.getSurgeRangeViewCount(currentTimeMs, -1));
        result.put("rangeViewCountForC", itemService.getSurgeRangeViewCount(currentTimeMs, -2));

        return result;
    }

}

