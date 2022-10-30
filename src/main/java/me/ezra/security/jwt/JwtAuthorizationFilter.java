package me.ezra.security.jwt;

import me.ezra.security.user.User;
import me.ezra.security.user.UserRegisterDto;
import me.ezra.security.user.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * JWT를 이용한 인증
 */
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    public JwtAuthorizationFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = null;

        try {
            // cookie에서 JWT Token을 가져옵니다.
            token = Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equals(JwtProperties.COOKIE_NAME))
                    .map(Cookie::getValue)
                    .findAny()
                    .orElse(null);

        } catch (Exception ignored) {}
        if (token != null) {
            try {
                Authentication authentication =
                        getUsernamePasswordAuthenticationToken(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                Cookie cookie = new Cookie(JwtProperties.COOKIE_NAME, null);
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
        filterChain.doFilter(request, response);
    }

    private Authentication getUsernamePasswordAuthenticationToken(String token) {
        // 유저를 유저명으로 찾습니다.
        String userName = JwtUtils.getUsername(token);
        User user = userRepository.findByUsername(userName).orElse(null);

        if (userName != null && user != null) {
            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        }
        // 유저가 없으면 NULL
        return null;
    }

}