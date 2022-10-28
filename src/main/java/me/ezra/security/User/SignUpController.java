package me.ezra.security.User;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/signup")
public class SignUpController {

    private final UserService userService;


    @GetMapping
    public String signupView() {
        return "signup";
    }

    @PostMapping
    public String signup(
            @ModelAttribute UserRegisterDto userRegisterDto
    ) {
        userService.signup(userRegisterDto.getUsername(), userRegisterDto.getPassword());
        return "redirect:login";
    }
}
