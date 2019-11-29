package com.gildedrose.api.controllers;

import com.gildedrose.api.services.ItemService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerTest {

    @Autowired
    ItemService itemService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void testTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/test").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("{\"response\":\"Greetings!\"}")));
    }

    @Test
    public void itemListTest() throws Exception {
        for(int i = 0; i<itemService.getSurgeTrigger(); i++)
            itemService.fetchAllItems_ForUserView();

        mvc.perform(MockMvcRequestBuilders.get("/items").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Frog leg")))
                .andExpect(content().string(startsWith("[")));

        System.err.println(status());

        System.out.println(content());
    }
}
