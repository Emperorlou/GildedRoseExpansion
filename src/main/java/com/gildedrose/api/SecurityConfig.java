package com.gildedrose.api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /*
        Here I want to configure the security so that anything put in the /users resource will
        automatically be secured and require authentication. I believe it is important that we don't rely
        on people adding authentication to each resource individually as they're liable to forget.
        Having this blanket makes it simple (imo) for everyone who might want to add to this project.
         */
        http
                .csrf().disable()
                .authorizeRequests()
                    .antMatchers("/user/**").authenticated()
                    .antMatchers("/**").permitAll()
                .and()
                    .httpBasic();


    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)
            throws Exception
    {
        /*
        In lieu of having a database, we're just going to add a couple users in-memory and utilize
        spring-boot's authentication systems to make things simple for this example.
         */
        auth.inMemoryAuthentication().withUser("user1").password("{noop}password").roles("USER");
        auth.inMemoryAuthentication().withUser("user2").password("{noop}password").roles("USER");
    }


}
