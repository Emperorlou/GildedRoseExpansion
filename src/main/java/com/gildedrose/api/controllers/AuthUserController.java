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

/**
 * This is the controller for user authenticated API calls. In reality we wouldn't want to use this
 * class for all authenticated API calls because it would get too bulky, but for the purpose of this test
 * it is sufficient.
 */
@RestController
public class AuthUserController {

    @Autowired
    ItemService itemService;

    /**
     * An authenticated test call you can ignore.
     * @return
     */
    @RequestMapping("/user/test")
    public String test() {
        String currentUserName = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserName = authentication.getName();
        }

        return "Greetings, "+currentUserName+"!";
    }

    /**
     * This is the buy item API call. It almost functions like a stub since it doesn't really do anything
     * special besides being in an authenticated area (/user/) and resulting in a positive message if the
     * item to buy was found. If the item given was not found then the exception thrown will cause a
     * 404 with an appropriate error message about the item not being found.
     *
     * Notes:
     *  - Since the Item datastructure we were given in the test did not include an ID, I've opted to use
     *  the item's name as an ID.
     *
     *
     * @param itemName
     * @return
     */
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

