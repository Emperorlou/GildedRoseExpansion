package com.gildedrose.api.controllers;

import com.gildedrose.api.controllers.dataentities.ItemData;
import com.gildedrose.api.dbentities.ItemEntity;
import com.gildedrose.api.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthUserController {

    @Autowired
    ItemService itemService;

    @RequestMapping("/user/test")
    public String test() {
        String currentUserName = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserName = authentication.getName();
        }

        return "Greetings, "+currentUserName+"!";
    }

    @RequestMapping("/user/buyitem")
    public Map buyItem(@RequestParam String itemName)
    {
        // Get the username of the currently logged in user
        String currentUserName = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserName = authentication.getName();
        }

        // Get the requested item, if it exists
        ItemData item = itemService.getItemByName(itemName);
        if (item==null) throw new ItemNotFoundException();

        // Generate the response
        Map<String, Object> response = new HashMap<>();

        // Since there are no merchant entities and no item quantities, it always succeeds
        response.put("status", "succeeded");
        response.put("costCents", item.getPrice());
        response.put("user", currentUserName);

        return response;
    }
}

