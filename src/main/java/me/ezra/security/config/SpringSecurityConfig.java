package me.ezra.security.config;

import lombok.RequiredArgsConstructor;
import me.ezra.security.user.UserService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Security 설정 Config
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {

    private final UserService userService;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                //정적 리소스 spring security 대상에서 제외
//                .mvcMatchers("/images/**", "/css/**"); // 아래와 같은 코드입니다.
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/home", "/index", "/signup", "/h2" +
                        "-console/**", "/console/**")
                .permitAll()
                .antMatchers("/post")
                .hasRole("USER")
                .antMatchers("/admin")
                .hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/notice/**")
                .hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/notice/**")
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

        http.httpBasic().disable(); //basic authentication filter 비활성화
        // Remember-Me
        http.rememberMe();
        // Csrf
        http.csrf();
        http.headers().frameOptions().disable();

        return http.build();
    }

}
