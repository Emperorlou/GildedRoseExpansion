package com.gildedrose.api.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthUserControllerTest {
    @Autowired
    private MockMvc mvc;

    /**
     * This is testing the spring-boot authentication setup.
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "user1")
    public void authenticatedTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/user/test").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Greetings, user1!")));
    }


    /**
     * This test checks to make sure a 401 error is thrown due to not being logged in.
     * @throws Exception
     */
    @Test
    public void unauthenticatedUserTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/user/buyitem?itemName=Frog leg").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * This test checks to make sure a 401 error is thrown due to not being logged in.
     * @throws Exception
     */
    @Test
    @WithMockUser(value = "user1")
    public void buyItem1Test() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/user/buyitem?itemName=Frog leg").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"response\":\"success\"")))
                .andExpect(content().string(containsString("\"costCents\":200,")));
    }
}
