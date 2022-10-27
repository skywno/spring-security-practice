package me.ezra.security.config;

import lombok.RequiredArgsConstructor;
import me.ezra.security.User.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.annotation.PostConstruct;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {

    private final UserService userService;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/css/**", "/home", "/index", "/example", "/signup", "/h2-console/**", "/console/**")
                    .permitAll()
                .antMatchers("/post")
                    .hasRole("USER")
                .antMatchers("/admin")
                    .hasRole("ADMIN")
                .anyRequest()
                    .authenticated()
                .and()
                    .formLogin() // enable form based log in
                    .loginPage("/login")
                    .defaultSuccessUrl("/")
                    .permitAll()
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/");

        http.csrf().disable();
        http.headers().frameOptions().disable();
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("user")
                .roles("USER")
                .build();
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("admin")
                .roles("ADMIN", "USER")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }
    @PostConstruct
    public void adminAccount() {
        userService.signup("user", "user");
        userService.signupAdmin("admin", "admin");
    }

}
