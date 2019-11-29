package com.gildedrose.api.controllers;

import com.gildedrose.api.controllers.dataentities.ItemData;
import com.gildedrose.api.dbentities.ItemEntity;
import com.gildedrose.api.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@RestController
public class ItemController {

    @Autowired
    ItemService itemService;

    @RequestMapping(value = "/test", produces = "application/json")
    public Map test() {
        return Collections.singletonMap("response", "Greetings!");
    }

    @RequestMapping(value = "/items", produces = "application/json")
    public Collection<ItemData> items() {
        return itemService.fetchAllItems_ForUserView();
    }
}

