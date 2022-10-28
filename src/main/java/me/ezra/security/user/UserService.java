package me.ezra.security.user;

import lombok.RequiredArgsConstructor;
import me.ezra.security.Exception.AlreadyRegisteredUserException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public User signup(
            String username,
            String password
    ) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new AlreadyRegisteredUserException();
        }
        return userRepository.save(new User(username,
                passwordEncoder.encode(password), "ROLE_USER"));
    }

    public User signupAdmin(
            String username,
            String password
    ) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new AlreadyRegisteredUserException();
        }
        return userRepository.save(new User(username,
                passwordEncoder.encode(password), "ROLE_ADMIN"));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("NOT FOUND"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("NOT FOUND"));

        return user;
    }
}
