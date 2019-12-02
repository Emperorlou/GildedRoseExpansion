package com.gildedrose.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.Collections;
import java.util.Map;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@RestController
public class LoginController {

    @Autowired
    AuthenticationManager authManager;

    @RequestMapping(value = "/login", produces = "application/json")
    public Map login(HttpServletRequest req, String user, String pass) {
        UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(user, pass);
        Authentication auth;
        try {
            auth = authManager.authenticate(authReq);
        }
        catch (AuthenticationException e) {
            return Collections.singletonMap("response", "failed");
        }

        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);
        HttpSession session = req.getSession(true);
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);

        return Collections.singletonMap("response", "success");
    }
}
