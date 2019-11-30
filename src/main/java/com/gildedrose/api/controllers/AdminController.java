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

    @RequestMapping(value = "/admin/config", produces = "application/json")
    public Map config() {
        Map<String, Object> result = new HashMap<>();

        result.put("surgeRange", itemService.getSurgeRange());
        result.put("surgeTrigger", itemService.getSurgeTrigger());
        result.put("surgePriceMultiplier", itemService.getSurgePriceMultiplier());
        result.put("currentRangeViewCount", itemService.getCurrentSurgeRangeViewCount());
        result.put("previousRangeViewCount", itemService.getPreviousSurgeRangeViewCount());

        return result;
    }

}

